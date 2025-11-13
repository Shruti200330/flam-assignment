package com.example.flam

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.TextureView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.opengl.GLSurfaceView
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var textureView: TextureView
    private lateinit var glSurface: GLSurfaceView
    private lateinit var glRenderer: GLRenderer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textureView = findViewById(R.id.textureView)
        glSurface = findViewById(R.id.glSurface)

        // Setup GLSurfaceView
        glSurface.setEGLContextClientVersion(2)
        glRenderer = GLRenderer(this)
        glSurface.setRenderer(glRenderer)
        glSurface.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        // Request camera permission
        val p = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (p != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        } else {
            startCameraPreview()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraPreview()
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startCameraPreview() {
        val preview = CameraPreview(this, textureView) { bitmap ->
            val w = bitmap.width
            val h = bitmap.height
            val buffer = java.nio.ByteBuffer.allocate(bitmap.byteCount)
            bitmap.copyPixelsToBuffer(buffer)
            val array = buffer.array()

            // Call native processor
            val processed = NativeLib.processFrame(array, w, h)
            if (processed != null) {
                glRenderer.updateFrame(processed, w, h)
                glSurface.requestRender()
            }
        }
        preview.start()
    }
}
