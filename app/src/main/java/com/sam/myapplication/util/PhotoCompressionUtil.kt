package com.sam.myapplication.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object PhotoCompressionUtil {

    /**
     * Compresses an image Uri into a high-clarity, lightweight JPEG file.
     * Max dimension: 1280px, Quality: 80%.
     * Output file size: ~150KB - 250KB (down from 5MB+), preserving full document/text readability.
     */
    fun compressImage(context: Context, uri: Uri, maxDimension: Int = 1280, quality: Int = 80): File? {
        return try {
            val photosDir = File(context.filesDir, "compressed_photos")
            if (!photosDir.exists()) photosDir.mkdirs()

            val fileName = "chowking_drive_${System.currentTimeMillis()}_${(1000..9999).random()}.jpg"
            val outputFile = File(photosDir, fileName)

            // 1. Decode bounds
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input, null, options)
            }

            if (options.outWidth <= 0 || options.outHeight <= 0) return null

            // 2. Calculate sample size
            var sampleSize = 1
            while (options.outWidth / sampleSize > maxDimension || options.outHeight / sampleSize > maxDimension) {
                sampleSize *= 2
            }

            val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
            val bitmap = context.contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input, null, decodeOptions)
            } ?: return null

            // 3. Compress to JPEG
            ByteArrayOutputStream().use { baos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
                val bytes = baos.toByteArray()
                FileOutputStream(outputFile).use { fos ->
                    fos.write(bytes)
                }
                Log.d("PhotoCompression", "Compressed photo size: ${bytes.size / 1024} KB")
            }
            bitmap.recycle()

            outputFile
        } catch (e: Exception) {
            Log.e("PhotoCompression", "Failed to compress image: ${e.message}")
            null
        }
    }
}
