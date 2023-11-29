package com.example.lcmdraw

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import yuku.ambilwarna.AmbilWarnaDialog

class MainActivity : AppCompatActivity() {

    private lateinit var drawingView: DrawingView
    private lateinit var imageView: ImageView
    private lateinit var drawHereText: TextView
    private lateinit var colorPickerButton: Button
    private val apiService = ApiService()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawingView)
        imageView = findViewById(R.id.imageView)
        drawHereText = findViewById(R.id.drawHereText)

        drawingView.setOnTouchListener { _, _ ->
            drawHereText.visibility = View.GONE
            false
        }

        // This is a simplified example. In a real app, you would likely want to send updates to the API less frequently
        drawingView.onDrawListener = { bitmap ->
            CoroutineScope(Dispatchers.IO).launch {
                val base64 = ImageUtils.bitmapToBase64(bitmap)
                val imageUrl = apiService.sendImage(base64)

                // Make sure to update the UI on the main thread
                runOnUiThread {
                    Glide.with(this@MainActivity)
                        .load(imageUrl)
                        .placeholder(imageView.drawable)
                        .into(imageView)
                }
            }
        }

        colorPickerButton = findViewById(R.id.colorPickerButton) // Assuming you have a button for color picker

        colorPickerButton.setOnClickListener {
            openColorPicker()
        }
    }

    private fun openColorPicker() {
        val colorPicker = AmbilWarnaDialog(this, drawingView.drawPaint.color, object : AmbilWarnaDialog.OnAmbilWarnaListener {
            override fun onCancel(dialog: AmbilWarnaDialog) {}

            override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                drawingView.drawPaint.color = color
            }
        })
        colorPicker.show()
    }
}