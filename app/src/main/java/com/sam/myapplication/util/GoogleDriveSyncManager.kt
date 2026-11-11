package com.sam.myapplication.util

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
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
                    if (driveUrl.isNotBlank()) driveUrl else Uri.fromFile(compressedFile).toString()
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
}
