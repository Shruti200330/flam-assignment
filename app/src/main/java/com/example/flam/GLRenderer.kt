package com.example.flam

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

object NativeLib {
    init {
        System.loadLibrary("native-lib")
    }
    external fun processFrame(inputRgba: ByteArray, w: Int, h: Int): ByteArray?
}

class GLRenderer(val ctx: Context) : GLSurfaceView.Renderer {

    private val vertexCoords = floatArrayOf(
        -1f, 1f,
        -1f, -1f,
         1f, 1f,
         1f, -1f
    )
    private lateinit var vbuf: FloatBuffer
    private var textureId = -1
    @Volatile private var pendingFrame: ByteArray? = null
    @Volatile private var frameW = 0
    @Volatile private var frameH = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        vbuf = ByteBuffer.allocateDirect(vertexCoords.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        vbuf.put(vertexCoords)
        vbuf.position(0)
        textureId = createTexture()
        Log.d("GLRenderer", "Surface created, texture=$textureId")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        Log.d("GLRenderer", "Surface changed: ${width}x${height}")
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        pendingFrame?.let { bytes ->
            try {
                // convert rgba bytes -> Bitmap and upload to texture
                val bmp = Bitmap.createBitmap(frameW, frameH, Config.ARGB_8888)
                val buf = ByteBuffer.wrap(bytes)
                bmp.copyPixelsFromBuffer(buf)
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0)
                bmp.recycle()
                Log.d("GLRenderer", "Frame uploaded: ${frameW}x${frameH}")
            } catch (e: Exception) {
                Log.e("GLRenderer", "Error updating frame", e)
            }
            pendingFrame = null
        }
    }

    fun updateFrame(bytes: ByteArray, w: Int, h: Int) {
        pendingFrame = bytes
        frameW = w
        frameH = h
    }

    private fun createTexture(): Int {
        val tex = IntArray(1)
        GLES20.glGenTextures(1, tex, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        return tex[0]
    }
}
