package com.sam.myapplication.supabase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.sam.myapplication.data.*
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class SupabaseStorageSyncManager(
    private val repository: AttendanceRepository,
    private val context: Context
) {
    private val client = SupabaseModule.client
    private val bucketName = "backups"
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
        coerceInputValues = true 
    }

    private fun getSafeFilename(empNo: String): String {
        return empNo.trim().replace("/", "_").replace("#", "_").replace(" ", "_")
    }

    private fun isImage(bytes: ByteArray): Boolean {
        if (bytes.size < 4) return false
        // Check for JPEG (FF D8 FF) or PNG (89 50 4E 47)
        val isJpeg = bytes[0] == 0xFF.toByte() && bytes[1] == 0xD8.toByte()
        val isPng = bytes[0] == 0x89.toByte() && bytes[1] == 0x50.toByte() && bytes[2] == 0x4E.toByte() && bytes[3] == 0x47.toByte()
        return isJpeg || isPng
    }

    suspend fun uploadEmployeePhoto(employeeId: String, uri: Uri): String? {
        Log.d("SupabaseStorage", "Uploading photo for employeeId: $employeeId, URI: $uri")
        try {
            val employee = repository.allEmployees.first().find { it.id == employeeId } ?: run {
                Log.e("SupabaseStorage", "Employee not found in repository for ID: $employeeId")
                return null
            }
            val empNo = employee.employeeNo ?: employeeId
            if (empNo.isBlank()) {
                Log.e("SupabaseStorage", "Employee number is blank for ID: $employeeId")
                return null
            }

            ensureBucketExists()
            
            // Process Image: Resize to 300x300 and compress for small size
            val bytes = processImage(uri, 300) ?: run {
                Log.e("SupabaseStorage", "Image processing failed for URI: $uri")
                return null
            }
            
            // Store locally first
            val imagesDir = File(context.filesDir, "profile_images")
            if (!imagesDir.exists()) imagesDir.mkdirs()
            val safeEmpNo = getSafeFilename(empNo)
            val localFile = File(imagesDir, "photo_$safeEmpNo.jpg")
            
            withContext(kotlinx.coroutines.Dispatchers.IO) {
                localFile.writeBytes(bytes)
            }
            
            // Upload to Supabase - Path: [SafeEmpNo]/[SafeEmpNo].jpg
            val fileName = "$safeEmpNo/$safeEmpNo.jpg"
            withContext(kotlinx.coroutines.Dispatchers.IO) {
                client.storage.from(bucketName).upload(fileName, bytes) { upsert = true }
            }
            Log.d("SupabaseStorage", "Uploaded photo to Supabase storage: $fileName")
            
            val publicUrl = client.storage.from(bucketName).publicUrl(fileName)
            repository.updateEmployee(employee.copy(profileImageUri = publicUrl))
            
            return publicUrl
        } catch (e: Exception) {
            Log.e("SupabaseStorage", "Photo upload failed: ${e.message}", e)
            return null
        }
    }

    private fun processImage(uri: Uri, targetSize: Int): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null
                
                // 1x1 Crop (Center Crop)
                val width = originalBitmap.width
                val height = originalBitmap.height
                val newSize = if (width > height) height else width
                val xOffset = (width - newSize) / 2
                val yOffset = (height - newSize) / 2
                
                val croppedBitmap = Bitmap.createBitmap(originalBitmap, xOffset, yOffset, newSize, newSize)
                
                // Resize to target size (e.g., 300x300)
                val scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, targetSize, targetSize, true)
                
                val outStream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outStream)
                
                val result = outStream.toByteArray()
                
                // Cleanup
                if (originalBitmap != croppedBitmap) originalBitmap.recycle()
                croppedBitmap.recycle()
                scaledBitmap.recycle()
                
                result
            }
        } catch (e: Exception) {
            Log.e("SupabaseStorage", "Image processing failed", e)
            null
        }
    }

    suspend fun downloadEmployeePhoto(employeeNo: String, employeeId: String): String? {
        Log.d("SupabaseStorage", "Attempting download for $employeeNo")
        try {
            val safeEmpNo = getSafeFilename(employeeNo)
            // Try different possible paths to handle legacy naming
            val pathsToTry = listOf(
                "$safeEmpNo/$safeEmpNo.jpg",
                "$safeEmpNo.jpg",
                "$employeeNo/$employeeNo.jpg",
                "$employeeNo.jpg",
                "$safeEmpNo/profile.jpg"
            )
            
            var imageBytes: ByteArray? = null
            for (path in pathsToTry) {
                try {
                    // Try authenticated download first (preferred as we have the key)
                    val bytes = client.storage.from(bucketName).downloadAuthenticated(path)
                    if (bytes.isNotEmpty()) {
                        if (isImage(bytes)) {
                            Log.d("SupabaseStorage", "Valid image found at $path (${bytes.size} bytes)")
                            imageBytes = bytes
                            break
                        }
                    }
                } catch (e: Exception) {
                    // Fallback to downloadPublic if authenticated fails (though less likely to succeed)
                    try {
                        val bytes = client.storage.from(bucketName).downloadPublic(path)
                        if (bytes.isNotEmpty() && isImage(bytes)) {
                            Log.d("SupabaseStorage", "Valid image found at $path (public) (${bytes.size} bytes)")
                            imageBytes = bytes
                            break
                        }
                    } catch (e2: Exception) {
                        continue
                    }
                }
            }

            if (imageBytes == null) {
                Log.e("SupabaseStorage", "No valid image found for $employeeNo after trying all paths")
                return null
            }

            val imagesDir = File(context.filesDir, "profile_images")
            if (!imagesDir.exists()) imagesDir.mkdirs()
            val localFile = File(imagesDir, "photo_$safeEmpNo.jpg")
            
            withContext(kotlinx.coroutines.Dispatchers.IO) {
                localFile.writeBytes(imageBytes)
            }
            
            return Uri.fromFile(localFile).toString()
        } catch (e: Exception) {
            Log.e("SupabaseStorage", "Download process failed for $employeeNo: ${e.message}")
            return null
        }
    }

    suspend fun syncData() {
        Log.d("SupabaseStorage", "Starting full JSON backup sync...")
        try {
            ensureBucketExists()
            val employees = repository.allEmployees.first()
            employees.forEach { emp ->
                syncEmployeeData(emp.id)
            }
        } catch (e: Exception) {
            Log.e("SupabaseStorage", "Full sync failed: ${e.message}")
        }
    }

    suspend fun syncEmployeeData(employeeId: String) {
        try {
            ensureBucketExists()
            val emp = repository.allEmployees.first().find { it.id == employeeId } ?: return
            val safeEmpNo = getSafeFilename(emp.employeeNo ?: employeeId)
            
            val att = repository.getAttendanceForEmployee(employeeId).first()
            val dtr = repository.getDTRForEmployee(employeeId).first()
            val req = repository.getRequestsForEmployee(employeeId).first()
            val messages = repository.allMessages.first().filter { it.senderId == employeeId || it.receiverId == employeeId }
            
            val data = mapOf(
                "employee" to json.encodeToJsonElement(emp),
                "attendance" to json.encodeToJsonElement(att),
                "dtr" to json.encodeToJsonElement(dtr),
                "requests" to json.encodeToJsonElement(req),
                "messages" to json.encodeToJsonElement(messages)
            )
            
            val bytes = json.encodeToString(data).toByteArray()
            client.storage.from(bucketName).upload("$safeEmpNo/backup.json", bytes) { upsert = false }
            Log.d("SupabaseStorage", "Backup JSON uploaded for $safeEmpNo")
        } catch (e: Exception) {
            // Silently fail if already exists - we don't want to overwrite newer server data with old local data on launch
            if (e.message?.contains("Already exists", ignoreCase = true) == true) {
                Log.d("SupabaseStorage", "Backup for $employeeId already exists, skipping overwrite")
            } else {
                Log.e("SupabaseStorage", "Employee sync failed: ${e.message}")
            }
        }
    }

    suspend fun deleteEmployeeStorage(employeeNo: String, employeeId: String) {
        try {
            val safeEmpNo = getSafeFilename(employeeNo)
            
            // Delete folder content
            val list = client.storage.from(bucketName).list("$safeEmpNo/")
            val names = list.map { "$safeEmpNo/${it.name}" }
            if (names.isNotEmpty()) {
                client.storage.from(bucketName).delete(names)
            }
            Log.d("SupabaseStorage", "Deleted storage folder for $safeEmpNo")
        } catch (e: Exception) {
            Log.e("SupabaseStorage", "Failed to delete storage: ${e.message}")
        }
    }

    private suspend fun ensureBucketExists() {
        try {
            Log.d("SupabaseStorage", "Checking bucket: $bucketName")
            client.storage.getBucket(bucketName)
        } catch (e: Exception) {
            Log.d("SupabaseStorage", "Bucket not found, creating: $bucketName")
            try {
                client.storage.createBucket(bucketName) {
                    public = true
                }
                Log.d("SupabaseStorage", "Bucket created successfully")
            } catch (ce: Exception) {
                Log.e("SupabaseStorage", "Failed to create bucket: ${ce.message}")
            }
        }
    }
}
