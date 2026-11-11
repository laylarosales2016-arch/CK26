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
            val uri = Uri.parse(uriString)

            val openStream: () -> InputStream? = {
                if (uri.scheme == "file") {
                    val file = File(uri.path ?: "")
                    if (file.exists()) file.inputStream() else null
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
        if (fileNameOrPath.startsWith("file://") || fileNameOrPath.startsWith("content://")) {
            val uri = Uri.parse(fileNameOrPath)
            if (uri.scheme == "file") {
                val file = File(uri.path ?: "")
                if (file.exists() && file.length() > 0) return fileNameOrPath
            } else {
                return fileNameOrPath
            }
        }

        val pureName = File(fileNameOrPath).name
        val localFile = File(File(context.filesDir, "photos"), pureName)
        if (localFile.exists() && localFile.length() > 0) {
            return Uri.fromFile(localFile).toString()
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
