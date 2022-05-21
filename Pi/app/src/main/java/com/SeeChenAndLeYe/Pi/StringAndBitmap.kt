package com.SeeChenAndLeYe.Pi

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.util.*

class StringAndBitmap {
    private lateinit var bitmap: Bitmap
    private lateinit var string: String

    fun string2Bitmap(string: String):Bitmap? {

        if (string == null)
            return null

        var bytes = Base64.decode(string, Base64.DEFAULT)
        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        return bitmap
    }

    fun Bitmap2String(bitmap: Bitmap): String? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bytes = stream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }
}