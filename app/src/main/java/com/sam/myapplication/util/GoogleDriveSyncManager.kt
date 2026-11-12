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
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DriveUploadState(
    val fileName: String,
    val isUploading: Boolean,
    val isComplete: Boolean,
    val isError: Boolean = false,
    val message: String = ""
)

object GoogleDriveSyncManager {

    private val _uploadState = MutableStateFlow<DriveUploadState?>(null)
    val uploadState: StateFlow<DriveUploadState?> = _uploadState.asStateFlow()

    fun clearUploadState() {
        _uploadState.value = null
    }

    // Configurable Google Apps Script Web App URL or Endpoint
    var googleDriveScriptUrl: String = "https://script.google.com/macros/s/AKfycbzoEbyxztmRyt6MHjgXXcmAd9K3jzEogFF90FYi4O50NxPghdWpCZYVzSDCvWbg4Kgy5g/exec"

    /**
     * Compresses and uploads a photo to Google Drive.
     * Returns Google Drive file URL or null if failed.
     */
    suspend fun uploadPhotoToGoogleDrive(context: Context, photoUri: Uri, customFileName: String? = null): String? {
        return withContext(Dispatchers.IO) {
            val rawFileName = customFileName ?: "photo_${System.currentTimeMillis()}.jpg"
            val fileName = rawFileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")

            _uploadState.value = DriveUploadState(fileName, isUploading = true, isComplete = false)

            try {
                val compressedFile = PhotoCompressionUtil.compressImage(context, photoUri) ?: run {
                    _uploadState.value = DriveUploadState(fileName, isUploading = false, isComplete = false, isError = true, message = "Compression failed")
                    return@withContext null
                }
                val bytes = compressedFile.readBytes()
                val base64Data = Base64.encodeToString(bytes, Base64.NO_WRAP)

                val payload = JSONObject().apply {
                    put("filename", fileName)
                    put("mimeType", "image/jpeg")
                    put("base64Data", base64Data)
                    put("folderName", "Chowking_Photos_Backup")
                }

                if (googleDriveScriptUrl.isBlank() || googleDriveScriptUrl.contains("chowking_drive_backup")) {
                    Log.w("GoogleDriveSync", "Google Drive script URL not set yet. Saving file locally.")
                    _uploadState.value = DriveUploadState(fileName, isUploading = false, isComplete = false, isError = true, message = "Script URL not set")
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
                    val finalUrl = if (driveUrl.isNotBlank()) convertToDirectDriveUrl(driveUrl) else Uri.fromFile(compressedFile).toString()
                    _uploadState.value = DriveUploadState(fileName, isUploading = false, isComplete = true, message = "Upload complete")
                    finalUrl
                } else {
                    Log.e("GoogleDriveSync", "Google Drive Upload HTTP Error: $responseCode")
                    _uploadState.value = DriveUploadState(fileName, isUploading = false, isComplete = false, isError = true, message = "HTTP $responseCode")
                    Uri.fromFile(compressedFile).toString()
                }
            } catch (e: Exception) {
                Log.e("GoogleDriveSync", "Failed to upload photo to Google Drive: ${e.message}")
                _uploadState.value = DriveUploadState(fileName, isUploading = false, isComplete = false, isError = true, message = e.message ?: "Upload failed")
                null
            }
        }
    }

    fun convertToDirectDriveUrl(url: String): String {
        if (url.isBlank()) return url
        var fileId: String? = null
        if (url.contains("drive.google.com/file/d/")) {
            fileId = url.substringAfter("file/d/").substringBefore("/").substringBefore("?")
        } else if (url.contains("drive.google.com/uc?") || url.contains("id=")) {
            fileId = url.substringAfter("id=").substringBefore("&")
        } else if (url.contains("drive.google.com/open?id=")) {
            fileId = url.substringAfter("open?id=").substringBefore("&")
        } else if (url.contains("lh3.googleusercontent.com/d/")) {
            fileId = url.substringAfter("lh3.googleusercontent.com/d/").substringBefore("?")
        }

        if (!fileId.isNullOrBlank()) {
            return "https://lh3.googleusercontent.com/d/$fileId"
        }
        return url
    }

    suspend fun uploadPhotoToGoogleDriveIfNotUploaded(
        context: Context,
        photoUriStr: String,
        customFileName: String? = null
    ): String {
        if (photoUriStr.isBlank()) return photoUriStr

        if (photoUriStr.contains("drive.google.com") || photoUriStr.contains("lh3.googleusercontent.com") || photoUriStr.startsWith("http://") || photoUriStr.startsWith("https://")) {
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
     * Downloads a photo from Google Drive URL or Filename into local app storage (filesDir/photos/)
     * so it's restored and viewable offline.
     */
    suspend fun downloadPhotoFromGoogleDrive(context: Context, photoUrlOrName: String): String? {
        if (photoUrlOrName.isBlank()) return null
        return withContext(Dispatchers.IO) {
            try {
                val pureFileName = try {
                    val path = Uri.parse(photoUrlOrName).path ?: photoUrlOrName
                    File(path).name
                } catch (e: Exception) {
                    File(photoUrlOrName).name
                }

                // 1. Check if photo already exists locally on disk
                val photosDir = File(context.filesDir, "photos")
                val localFile = File(photosDir, pureFileName)
                if (localFile.exists() && localFile.length() > 0) {
                    return@withContext Uri.fromFile(localFile).toString()
                }

                var bytes: ByteArray? = null

                // 2. Download by Direct URL if it's an HTTP/HTTPS link
                if (photoUrlOrName.startsWith("http://") || photoUrlOrName.startsWith("https://")) {
                    val downloadUrl = convertToDirectDriveUrl(photoUrlOrName)
                    Log.d("GoogleDriveSync", "Downloading photo from Drive URL: $downloadUrl")
                    val url = URL(downloadUrl)
                    val conn = (url.openConnection() as HttpURLConnection).apply {
                        instanceFollowRedirects = true
                        connectTimeout = 15000
                        readTimeout = 15000
                    }
                    bytes = conn.inputStream.use { it.readBytes() }
                }

                // 3. Fallback: Query Google Apps Script Web App to search Google Drive by filename
                if ((bytes == null || bytes.isEmpty()) && googleDriveScriptUrl.isNotBlank() && pureFileName.isNotBlank()) {
                    Log.d("GoogleDriveSync", "Searching Google Drive by filename: $pureFileName")
                    val payload = JSONObject().apply {
                        put("action", "download")
                        put("filename", pureFileName)
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
                    if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                        val responseStr = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
                        val jsonResp = JSONObject(responseStr)
                        if (jsonResp.optString("status") == "SUCCESS") {
                            val base64Str = jsonResp.optString("base64Data", "")
                            if (base64Str.isNotBlank()) {
                                bytes = Base64.decode(base64Str, Base64.NO_WRAP)
                            }
                        }
                    }
                }

                if (bytes == null || bytes.isEmpty()) {
                    Log.e("GoogleDriveSync", "Photo not found in Google Drive: $pureFileName")
                    return@withContext null
                }

                if (!photosDir.exists()) photosDir.mkdirs()
                val destFile = File(photosDir, pureFileName)
                FileOutputStream(destFile).use { fos ->
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
