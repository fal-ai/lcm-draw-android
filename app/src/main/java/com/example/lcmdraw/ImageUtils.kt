package com.example.lcmdraw
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageUtils {

    fun bitmapToBase64(bitmap: Bitmap): String {
        // Create a new bitmap with the same dimensions as your original bitmap
        val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        // Create a new canvas to draw onto the new bitmap
        val canvas = Canvas(newBitmap)

        // Draw a white background onto the canvas
        canvas.drawColor(Color.WHITE)

        // Draw your original bitmap onto the canvas
        canvas.drawBitmap(bitmap, 0f, 0f, null)

        val byteArrayOutputStream = ByteArrayOutputStream()

        // Compress the new bitmap, not the original one
        newBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

        val byteArray = byteArrayOutputStream.toByteArray()
        val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

        // Return the base64 string as a data URI
        return "data:image/png;base64,$base64String"
    }


    fun base64ToBitmap(base64String: String): Bitmap {
        // Remove the "data:image/png;base64," part to get the actual base64 string
        val pureBase64Encoded = base64String.substring(base64String.indexOf(",") + 1)
        val decodedString = Base64.decode(pureBase64Encoded, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}