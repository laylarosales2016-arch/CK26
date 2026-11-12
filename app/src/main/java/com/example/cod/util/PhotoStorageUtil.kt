package com.example.cod.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object PhotoStorageUtil {

    fun saveImageToAppStorage(context: Context, uri: Uri): String {
        return try {
            val photosDir = File(context.filesDir, "photos")
            if (!photosDir.exists()) photosDir.mkdirs()

            val fileName = "cod_photo_${System.currentTimeMillis()}_${(1000..9999).random()}.jpg"
            val destFile = File(photosDir, fileName)

            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input, null, options)
            }

            val maxDim = 1024
            var sampleSize = 1
            while (options.outWidth / sampleSize > maxDim || options.outHeight / sampleSize > maxDim) {
                sampleSize *= 2
            }

            val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
            val bitmap = context.contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input, null, decodeOptions)
            }

            if (bitmap != null) {
                FileOutputStream(destFile).use { output ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
                }
                bitmap.recycle()
                Uri.fromFile(destFile).toString()
            } else {
                uri.toString()
            }
        } catch (_: Throwable) {
            uri.toString()
        }
    }

    fun loadThumbnailBitmap(context: Context, uriString: String, targetSize: Int = 300): Bitmap? {
        if (uriString.isBlank()) return null
        return try {
            var resolvedStr = resolveLocalPhotoUri(context, uriString) ?: uriString

            val isFile = resolvedStr.startsWith("file://") || resolvedStr.startsWith("/")
            val existsLocally = if (isFile) {
                val path = if (resolvedStr.startsWith("file://")) resolvedStr.substring(7) else resolvedStr
                val f = File(path)
                f.exists() && f.length() > 0
            } else false

            if (!existsLocally && !resolvedStr.startsWith("content://")) {
                val downloaded = try {
                    kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
                        com.sam.myapplication.util.GoogleDriveSyncManager.downloadPhotoFromGoogleDrive(context, uriString)
                    }
                } catch (_: Exception) { null }
                if (!downloaded.isNullOrBlank()) {
                    resolvedStr = downloaded
                }
            }

            val uri = Uri.parse(resolvedStr)

            val openStream: () -> InputStream? = {
                if (uri.scheme == "file") {
                    val file = File(uri.path ?: "")
                    if (file.exists() && file.length() > 0) file.inputStream() else null
                } else if (uri.scheme == "http" || uri.scheme == "https") {
                    java.net.URL(resolvedStr).openStream()
                } else {
                    context.contentResolver.openInputStream(uri)
                }
            }

            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            openStream()?.use { input ->
                BitmapFactory.decodeStream(input, null, options)
            }

            if (options.outWidth <= 0 || options.outHeight <= 0) return null

            var sampleSize = 1
            while (options.outWidth / sampleSize > targetSize || options.outHeight / sampleSize > targetSize) {
                sampleSize *= 2
            }

            val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
            openStream()?.use { input ->
                BitmapFactory.decodeStream(input, null, decodeOptions)
            }
        } catch (_: Throwable) {
            null
        }
    }

    fun getPhotoFileName(uriString: String?): String? {
        if (uriString.isNullOrBlank()) return null
        return try {
            val uri = Uri.parse(uriString)
            if (uri.scheme == "file") {
                File(uri.path ?: "").name
            } else {
                val lastSeg = uri.lastPathSegment ?: ""
                if (lastSeg.contains("/")) File(lastSeg).name else lastSeg
            }
        } catch (_: Throwable) {
            null
        }
    }

    fun resolveLocalPhotoUri(context: Context, fileNameOrPath: String?): String? {
        if (fileNameOrPath.isNullOrBlank()) return null

        val pureName = try {
            val path = Uri.parse(fileNameOrPath).path ?: fileNameOrPath
            File(path).name
        } catch (_: Exception) {
            File(fileNameOrPath).name
        }

        val photoFile = File(File(context.filesDir, "photos"), pureName)
        if (photoFile.exists() && photoFile.length() > 0) {
            return Uri.fromFile(photoFile).toString()
        }

        val compressedFile = File(File(context.filesDir, "compressed_photos"), pureName)
        if (compressedFile.exists() && compressedFile.length() > 0) {
            return Uri.fromFile(compressedFile).toString()
        }

        if (fileNameOrPath.startsWith("file://") || fileNameOrPath.startsWith("/")) {
            val pathStr = if (fileNameOrPath.startsWith("file://")) fileNameOrPath.substring(7) else fileNameOrPath
            val directFile = File(pathStr)
            if (directFile.exists() && directFile.length() > 0) {
                return Uri.fromFile(directFile).toString()
            }
        }

        return fileNameOrPath
    }

    fun photoUriToBase64(context: Context, uriString: String?): String? {
        if (uriString.isNullOrBlank()) return null
        return try {
            val uri = Uri.parse(uriString)
            val inputStream = if (uri.scheme == "file") {
                val file = File(uri.path ?: "")
                if (file.exists()) file.inputStream() else null
            } else {
                context.contentResolver.openInputStream(uri)
            }
            inputStream?.use { input ->
                val bytes = input.readBytes()
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            }
        } catch (_: Throwable) {
            null
        }
    }

    fun base64ToPhotoUri(context: Context, base64String: String?): String? {
        if (base64String.isNullOrBlank()) return null

        if (base64String.startsWith("file://") || base64String.startsWith("content://")) {
            val uri = Uri.parse(base64String)
            if (uri.scheme == "file") {
                val file = File(uri.path ?: "")
                if (file.exists() && file.length() > 0) {
                    return base64String
                }
            }
        }

        return try {
            val bytes = Base64.decode(base64String, Base64.NO_WRAP)
            val photosDir = File(context.filesDir, "photos")
            if (!photosDir.exists()) photosDir.mkdirs()

            val fileName = "cod_restored_${System.currentTimeMillis()}_${(1000..9999).random()}.jpg"
            val destFile = File(photosDir, fileName)

            FileOutputStream(destFile).use { output ->
                output.write(bytes)
            }
            Uri.fromFile(destFile).toString()
        } catch (_: Throwable) {
            null
        }
    }
}
