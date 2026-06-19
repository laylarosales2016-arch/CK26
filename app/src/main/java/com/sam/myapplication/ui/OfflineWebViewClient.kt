package com.sam.myapplication.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.webkit.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import java.net.URLConnection

/**
 * Custom WebViewClient that intercepts and caches network resources (CSS, JS, Images)
 * for offline viewing of permits.
 */
class OfflineWebViewClient(
    private val context: Context,
    private val onPageStarted: (String?) -> Unit = {},
    private val onPageFinished: (String?) -> Unit = {},
    private val onLoginTrigger: (WebView?) -> Unit = {},
    private val onScrapeTrigger: (WebView?) -> Unit = {}
) : WebViewClient() {

    private val client = OkHttpClient()
    private val assetCacheDir = File(context.cacheDir, "permit_assets").apply { if (!exists()) mkdirs() }

    private fun isOnline(): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            false
        }
    }

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        val url = request?.url?.toString() ?: return null
        
        // Only intercept common assets to avoid caching dynamic page content that might change
        val extension = url.substringAfterLast('.', "").split('?')[0].split('#')[0].lowercase()
        val assetExtensions = listOf("css", "js", "png", "jpg", "jpeg", "gif", "svg", "woff", "woff2", "ttf")
        
        if (!assetExtensions.contains(extension)) return null

        val cacheFile = File(assetCacheDir, "${url.hashCode()}.$extension")

        // 1. Return from cache if exists (Offline mode)
        if (cacheFile.exists()) {
            return try {
                val mimeType = URLConnection.guessContentTypeFromName(cacheFile.name) ?: "application/octet-stream"
                WebResourceResponse(mimeType, "UTF-8", FileInputStream(cacheFile))
            } catch (e: Exception) {
                null
            }
        }

        // 2. If online, fetch and save to cache for future offline use
        if (isOnline()) {
            try {
                val okRequest = Request.Builder().url(url).build()
                client.newCall(okRequest).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body
                        if (body != null) {
                            val bytes = body.bytes()
                            FileOutputStream(cacheFile).use { it.write(bytes) }
                            val mimeType = body.contentType()?.toString()?.substringBefore(";") ?: "application/octet-stream"
                            return WebResourceResponse(mimeType, "UTF-8", ByteArrayInputStream(bytes))
                        }
                    }
                }
            } catch (e: Exception) {
                // Silently fail and let the webview handle the request normally if possible
                Log.w("OfflineWebView", "Failed to cache $url: ${e.message}")
            }
        }

        return null
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        onPageStarted(url)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        onPageFinished(url)
        if (url != null && (url.contains("Login") || url.endsWith(".com/"))) {
            onLoginTrigger(view)
        }
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: android.net.http.SslError?) {
        handler?.proceed()
    }
}
