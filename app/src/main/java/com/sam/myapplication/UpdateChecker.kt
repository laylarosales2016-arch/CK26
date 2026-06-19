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

@Serializable
data class UpdateInfo(
    val versionCode: Int,
    val versionName: String,
    val downloadUrl: String,
    val isForceUpdate: Boolean,
    val changes: String? = null
)

object UpdateChecker {
    private const val UPDATE_JSON_URL = "https://uhmjgnwpzsemksxcjpiz.supabase.co/storage/v1/object/public/updates/update_config.json"

    suspend fun checkForUpdates(activity: Activity) {
        val updateInfo = fetchUpdateInfo() ?: return

        val currentVersionCode = try {
            val pInfo = activity.packageManager.getPackageInfo(activity.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode
            }
        } catch (e: Exception) {
            0
        }

        if (updateInfo.versionCode > currentVersionCode) {
            withContext(Dispatchers.Main) {
                showUpdateDialog(activity, updateInfo)
            }
        }
    }

    private suspend fun fetchUpdateInfo(): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val url = URL(UPDATE_JSON_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val jsonString = connection.inputStream.bufferedReader().use { it.readText() }
                val json = Json { ignoreUnknownKeys = true }
                json.decodeFromString<UpdateInfo>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
