package com.sam.myapplication.util

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import com.example.cod.util.PhotoStorageUtil
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object GoogleDriveSyncManager {

    // Configurable Google Apps Script Web App URL or Endpoint
    var googleDriveScriptUrl: String = "https://script.google.com/macros/s/AKfycbzoEbyxztmRyt6MHjgXXcmAd9K3jzEogFF90FYi4O50NxPghdWpCZYVzSDCvWbg4Kgy5g/exec"

    /**
     * Compresses and uploads a photo to Google Drive.
     * Returns Google Drive file URL or null if failed.
     */
    suspend fun uploadPhotoToGoogleDrive(context: Context, photoUri: Uri, customFileName: String? = null): String? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Compress image to ~200KB crystal-clear JPEG (max 1280px, 80% quality)
                val compressedFile = PhotoCompressionUtil.compressImage(context, photoUri) ?: return@withContext null
                val bytes = compressedFile.readBytes()
                val base64Data = Base64.encodeToString(bytes, Base64.NO_WRAP)

                val fileName = customFileName ?: compressedFile.name

                // 2. Prepare JSON payload
                val payload = JSONObject().apply {
                    put("filename", fileName)
                    put("mimeType", "image/jpeg")
                    put("base64Data", base64Data)
                    put("folderName", "Chowking_Photos_Backup")
                }

                // 3. Post to Google Drive Web App endpoint
                if (googleDriveScriptUrl.isBlank() || googleDriveScriptUrl.contains("chowking_drive_backup")) {
                    Log.w("GoogleDriveSync", "Google Drive script URL not set yet. Saving file locally.")
                    return@withContext Uri.fromFile(compressedFile).toString()
                }

                val url = URL(googleDriveScriptUrl)
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    doInput = true
                    setRequestProperty("Content-Type", "application/json")
                    connectTimeout = 15000
                    readTimeout = 15000
                }

                OutputStreamWriter(conn.outputStream).use { writer ->
                    writer.write(payload.toString())
                    writer.flush()
                }

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    val responseStr = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
                    val jsonResp = JSONObject(responseStr)
                    val driveUrl = jsonResp.optString("fileUrl", jsonResp.optString("url", ""))
                    Log.d("GoogleDriveSync", "Photo successfully uploaded to Google Drive: $driveUrl")
                    if (driveUrl.isNotBlank()) convertToDirectDriveUrl(driveUrl) else Uri.fromFile(compressedFile).toString()
                } else {
                    Log.e("GoogleDriveSync", "Google Drive Upload HTTP Error: $responseCode")
                    Uri.fromFile(compressedFile).toString()
                }
            } catch (e: Exception) {
                Log.e("GoogleDriveSync", "Failed to upload photo to Google Drive: ${e.message}")
                null
            }
        }
    }

    fun convertToDirectDriveUrl(url: String): String {
        if (url.isBlank()) return url
        if (url.contains("drive.google.com/file/d/")) {
            val fileId = url.substringAfter("file/d/").substringBefore("/")
            return "https://drive.google.com/uc?export=download&id=$fileId"
        }
        if (url.contains("drive.google.com/open?id=")) {
            val fileId = url.substringAfter("open?id=").substringBefore("&")
            return "https://drive.google.com/uc?export=download&id=$fileId"
        }
        return url
    }

    suspend fun uploadPhotoToGoogleDriveIfNotUploaded(
        context: Context,
        photoUriStr: String,
        customFileName: String? = null
    ): String {
        if (photoUriStr.isBlank()) return photoUriStr

        if (photoUriStr.contains("drive.google.com") || photoUriStr.startsWith("http://") || photoUriStr.startsWith("https://")) {
            Log.d("GoogleDriveSync", "Photo already uploaded to Google Drive. Skipping duplicate upload: $photoUriStr")
            return convertToDirectDriveUrl(photoUriStr)
        }

        return try {
            val uri = Uri.parse(photoUriStr)
            val uploadedUrl = uploadPhotoToGoogleDrive(context, uri, customFileName)
            if (!uploadedUrl.isNullOrBlank() && (uploadedUrl.startsWith("http://") || uploadedUrl.startsWith("https://"))) {
                convertToDirectDriveUrl(uploadedUrl)
            } else {
                photoUriStr
            }
        } catch (e: Exception) {
            Log.e("GoogleDriveSync", "Error uploading photo to Drive: ${e.message}")
            photoUriStr
        }
    }

    /**
     * Downloads a photo from Google Drive URL into local app storage (filesDir/photos/)
     * so it's restored and viewable offline.
     */
    suspend fun downloadPhotoFromGoogleDrive(context: Context, photoUrlOrName: String): String? {
        if (photoUrlOrName.isBlank()) return null
        return withContext(Dispatchers.IO) {
            try {
                // If it's already a working local file, return it
                val existingLocal = PhotoStorageUtil.resolveLocalPhotoUri(context, photoUrlOrName)
                if (existingLocal != null && existingLocal.startsWith("file://")) {
                    val path = Uri.parse(existingLocal).path
                    if (path != null && File(path).exists() && File(path).length() > 0) {
                        return@withContext existingLocal
                    }
                }

                if (!photoUrlOrName.startsWith("http://") && !photoUrlOrName.startsWith("https://")) {
                    return@withContext photoUrlOrName
                }

                var downloadUrl = photoUrlOrName
                if (photoUrlOrName.contains("drive.google.com/file/d/")) {
                    val fileId = photoUrlOrName.substringAfter("file/d/").substringBefore("/")
                    downloadUrl = "https://drive.google.com/uc?export=download&id=$fileId"
                }

                Log.d("GoogleDriveSync", "Downloading photo from Drive URL: $downloadUrl")

                val url = URL(downloadUrl)
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = true
                    connectTimeout = 15000
                    readTimeout = 15000
                }

                val bytes = conn.inputStream.use { it.readBytes() }
                if (bytes.isEmpty()) return@withContext null

                val photosDir = File(context.filesDir, "photos")
                if (!photosDir.exists()) photosDir.mkdirs()

                val pureFileName = try {
                    File(Uri.parse(photoUrlOrName).path ?: "").name
                } catch (e: Exception) { "" }

                val fileName = if (pureFileName.isNotBlank() && pureFileName.contains(".")) {
                    pureFileName
                } else {
                    "cod_drive_restored_${System.currentTimeMillis()}_${(1000..9999).random()}.jpg"
                }

                val destFile = File(photosDir, fileName)
                java.io.FileOutputStream(destFile).use { fos ->
                    fos.write(bytes)
                }

                val localFileUri = Uri.fromFile(destFile).toString()
                Log.d("GoogleDriveSync", "Successfully downloaded photo from Drive to: $localFileUri")
                localFileUri
            } catch (e: Exception) {
                Log.e("GoogleDriveSync", "Failed to download photo from Google Drive: ${e.message}")
                null
            }
        }
    }
}
