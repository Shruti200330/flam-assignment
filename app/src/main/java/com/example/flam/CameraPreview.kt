package com.example.flam

import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.util.Log
import android.view.TextureView
import android.hardware.Camera
import java.util.concurrent.Executors

class CameraPreview(private val ctx: Context, private val textureView: TextureView, private val onBitmap: (Bitmap) -> Unit) {

    private var camera: Camera? = null
    private val exec = Executors.newSingleThreadScheduledExecutor()

    fun start() {
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(st: SurfaceTexture, width: Int, height: Int) {
                try {
                    camera = Camera.open(0)
                    camera?.apply {
                        setPreviewTexture(st)
                        val lp = parameters
                        lp.setPreviewSize(width, height)
                        parameters = lp
                        startPreview()
                    }

                    // schedule repeated capture of TextureView bitmap
                    exec.scheduleAtFixedRate({
                        if (textureView.isAvailable) {
                            val bmp = textureView.getBitmap()
                            if (bmp != null) {
                                onBitmap(bmp)
                            }
                        }
                    }, 0, 100, java.util.concurrent.TimeUnit.MILLISECONDS) // ~10 fps
                } catch (e: Exception) {
                    Log.e("CameraPreview", "err", e)
                }
            }

            override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, width: Int, height: Int) {}
            override fun onSurfaceTextureDestroyed(st: SurfaceTexture): Boolean {
                camera?.stopPreview()
                camera?.release()
                camera = null
                exec.shutdown()
                return true
            }
            override fun onSurfaceTextureUpdated(st: SurfaceTexture) {}
        }
    }

    fun stop() {
        camera?.stopPreview()
        camera?.release()
    }
}
