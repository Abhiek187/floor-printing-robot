package com.example.linuxtest

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread

class CustomDraw (context: Context) : View(context) {
    private val access = context as MainActivity
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var paths = arrayListOf(Path(),Path(),Path(),Path(),Path())
    private var xCoord = 0f
    private var yCoord = 0f
    private var mBitmap: Bitmap? = null // loaded drawing

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action and MotionEvent.ACTION_MASK){
            MotionEvent.ACTION_DOWN,MotionEvent.ACTION_POINTER_DOWN -> {
                val ids = event.actionIndex
                if (ids > 4) return false
                xCoord = event.getX(ids)
                yCoord = event.getY(ids)
                paths[ids].moveTo(xCoord,yCoord)
            }
            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    xCoord = event.getX(i)
                    yCoord = event.getY(i)
                    paths[i].lineTo(xCoord,yCoord)
                }
            }
        }

        invalidate()
        performClick()
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.style = Paint.Style.STROKE
        paths.forEach { path -> canvas.drawPath(path, paint) }

        mBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        access.clear.setOnClickListener {
            paths.forEach { path -> path.reset() }
            mBitmap = null // clear the bitmap as well
            invalidate()
        }
    }

    fun saveDrawing(image: String) {
        // Save drawing as a bitmap and convert it to a PNG file
        val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        val file = File("${access.filesDir.path}/$image")

        try {
            thread {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(file))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadDrawing(path: String) {
        // Clear the previous drawing and load the image as a bitmap
        paths.forEach { p -> p.reset() }
        mBitmap = BitmapFactory.decodeFile(path) // immutable bitmap
        invalidate()
    }
}
