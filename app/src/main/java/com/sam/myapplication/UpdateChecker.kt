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

    suspend fun checkForUpdates(activity: Activity) {
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
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateInfo.downloadUrl))
                activity.startActivity(intent)
                if (updateInfo.isForceUpdate) {
                    activity.finishAffinity()
                }
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
            builder.setNegativeButton("Later", null)
            builder.setCancelable(true)
        }

        builder.show()
    }
}
