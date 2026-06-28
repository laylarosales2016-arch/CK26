package com.sam.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.app.AlertDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

import android.util.Log
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

@Serializable
data class UpdateInfo(
    val versionCode: Int,
    val versionName: String,
    val downloadUrl: String,
    val isForceUpdate: Boolean,
    val changes: String? = null
)

object UpdateChecker {
    private const val TAG = "UpdateChecker"
    // URL to your update_config.json file in your GitHub repository
    private const val UPDATE_JSON_URL = "https://raw.githubusercontent.com/laylarosales2016-arch/CK26/master/update_config.json"

    private var isDialogShowing = false

    suspend fun checkForUpdates(activity: Activity) {
        if (isDialogShowing) return

        Log.d(TAG, "Checking for updates at $UPDATE_JSON_URL")
        val updateInfo = fetchUpdateInfo()
        
        if (updateInfo == null) {
            Log.e(TAG, "Failed to fetch update info")
            return
        }

        val currentVersionCode = try {
            val pInfo = activity.packageManager.getPackageInfo(activity.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting version info", e)
            0
        }

        Log.d(TAG, "Current Version: $currentVersionCode, Remote Version: ${updateInfo.versionCode}")

        if (updateInfo.versionCode > currentVersionCode) {
            Log.i(TAG, "New version available: ${updateInfo.versionName}")
            withContext(Dispatchers.Main) {
                showUpdateDialog(activity, updateInfo)
            }
        } else {
            Log.d(TAG, "App is up to date")
        }
    }

    private suspend fun fetchUpdateInfo(): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val url = URL(UPDATE_JSON_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.useCaches = false
            
            Log.d(TAG, "Fetching remote JSON...")
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val jsonString = connection.inputStream.bufferedReader().use { it.readText() }
                Log.d(TAG, "JSON received: $jsonString")
                val json = Json { ignoreUnknownKeys = true }
                json.decodeFromString<UpdateInfo>(jsonString)
            } else {
                Log.e(TAG, "HTTP error: ${connection.responseCode}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fetch failed", e)
            null
        }
    }

    private fun showUpdateDialog(activity: Activity, updateInfo: UpdateInfo) {
        isDialogShowing = true
        val message = StringBuilder()
        message.append("A new version of Chowking Employee is available.\n\n")
        message.append("Version: ${updateInfo.versionName} (${updateInfo.versionCode})\n")
        
        if (!updateInfo.changes.isNullOrBlank()) {
            message.append("\nWhat's New:\n${updateInfo.changes}\n")
        }
        
        message.append("\nPlease update to continue.")

        val builder = AlertDialog.Builder(activity)
            .setTitle("Update Available")
            .setMessage(message.toString())
            .setPositiveButton("Update Now") { _, _ ->
                isDialogShowing = false
                startDownload(activity, updateInfo)
            }

        if (updateInfo.isForceUpdate) {
            builder.setCancelable(false)
            // Handle back button to close app
            builder.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
                    activity.finishAffinity()
                    true
                } else false
            }
        } else {
            builder.setNegativeButton("Later") { _, _ -> isDialogShowing = false }
            builder.setOnCancelListener { isDialogShowing = false }
            builder.setCancelable(true)
        }

        builder.show()
    }

    private fun startDownload(activity: Activity, updateInfo: UpdateInfo) {
        val downloadManager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(updateInfo.downloadUrl)
        
        // Clear old update file if it exists
        val oldFile = File(activity.cacheDir, "update.apk")
        if (oldFile.exists()) oldFile.delete()

        val request = DownloadManager.Request(uri)
            .setTitle("Downloading Chowking Update")
            .setDescription("New version ${updateInfo.versionName}")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(activity, null, "update.apk") // Keep this but ensure file_paths is correct
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadId = downloadManager.enqueue(request)

        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = downloadManager.query(query)
                    if (cursor.moveToFirst()) {
                        val statusIdx = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        val reasonIdx = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                        
                        if (statusIdx != -1) {
                            val status = cursor.getInt(statusIdx)
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                installApk(activity)
                            } else if (reasonIdx != -1) {
                                val reason = cursor.getInt(reasonIdx)
                                Log.e(TAG, "Download failed with reason: $reason")
                            }
                        }
                    }
                    cursor.close()
                    activity.unregisterReceiver(this)
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            activity.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
    }

    private fun installApk(activity: Activity) {
        val file = File(activity.getExternalFilesDir(null), "update.apk")
        if (file.exists()) {
            // On Android 8.0+, we must have the REQUEST_INSTALL_PACKAGES permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!activity.packageManager.canRequestPackageInstalls()) {
                    activity.startActivity(Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${activity.packageName}")
                    })
                    Toast.makeText(activity, "Please enable 'Install unknown apps' and then click update again.", Toast.LENGTH_LONG).show()
                    return
                }
            }

            val contentUri = FileProvider.getUriForFile(
                activity,
                "${activity.packageName}.fileprovider",
                file
            )
            val installIntent = Intent(Intent.ACTION_VIEW)
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            installIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            installIntent.setDataAndType(contentUri, "application/vnd.android.package-archive")
            
            try {
                activity.startActivity(installIntent)
            } catch (e: Exception) {
                Log.e(TAG, "Installation failed", e)
                Toast.makeText(activity, "Installation failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
