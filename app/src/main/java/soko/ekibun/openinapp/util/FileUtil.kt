package soko.ekibun.openinapp.util

import android.provider.MediaStore
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object FileUtil{
    fun saveBitmapToCache(context: Context, bmp: Bitmap, fileName: String): File? {
        var file: File? = null
        try {
            val fileFolder = getDiskCacheDir(context, "Screenshots")
            deleteFile(context)
            if (!fileFolder.exists())
                fileFolder.mkdirs()

            file = File(fileFolder.absolutePath, fileName)
            if (!file.exists())
                file.createNewFile()
            val outStream = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            outStream.flush()
            outStream.close()

            val values = ContentValues().apply {
                put(MediaStore.Images.ImageColumns.DATA, file.absolutePath)
                put(MediaStore.Images.ImageColumns.TITLE, "QRCODE")
                put(MediaStore.Images.ImageColumns.DISPLAY_NAME, file.name)
                put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/png")
                put(MediaStore.Images.ImageColumns.SIZE, file.length())
            }
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } catch (e: IOException) { e.printStackTrace() }
        return file
    }

    fun deleteFile(context: Context) {
        val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, "TITLE = 'QRCODE'", null, null)
        while(cursor?.moveToNext() == true) {
            val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
            Log.v("file", path)
            val file = File(path)
            if (!file.exists()) file.delete()
        }
        cursor?.close()
        context.contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "TITLE = 'QRCODE'", null)
    }

    private fun getDiskCacheDir(context: Context, uniqueName: String): File {
        val cachePath: String = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable())
            (context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?:context.externalCacheDir)!!.path
        else
            context.cacheDir.path
        return File(cachePath + File.separator + uniqueName)
    }
}