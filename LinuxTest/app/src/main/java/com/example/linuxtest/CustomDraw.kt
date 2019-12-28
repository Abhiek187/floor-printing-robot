package com.example.linuxtest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread

class CustomDraw (context: Context) : View(context){
    private val access = context as MainActivity
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var path = arrayListOf(Path(),Path(),Path(),Path(),Path())
    private var xCoord = 0f
    private var yCoord = 0f
    private lateinit var mCanvas: Canvas

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //return super.onTouchEvent(event)
        when(event.action and MotionEvent.ACTION_MASK){
            MotionEvent.ACTION_DOWN,MotionEvent.ACTION_POINTER_DOWN ->{
                val ids = event.actionIndex
                if(ids > 4) return false
                xCoord = event.getX(ids)
                yCoord = event.getY(ids)
                path[ids].moveTo(xCoord,yCoord)
            }
            MotionEvent.ACTION_MOVE -> {
                for(i in 0 until event.pointerCount){
                    xCoord = event.getX(i)
                    yCoord = event.getY(i)
                    path[i].lineTo(xCoord,yCoord)
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
        mCanvas = canvas
        paint.style = Paint.Style.STROKE

        for(i in 0 until path.size){
            canvas.drawPath(path[i], paint)
        }

        access.clear.setOnClickListener{
            for(i in 0 until path.size){
                path[i].reset()
            }
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

    /*fun loadDrawing(path: String) {
        val bitmap = BitmapFactory.decodeFile(path) // immutable bitmap
        /*val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        draw(canvas)*/
        val drawable: Drawable = BitmapDrawable(resources, bitmap)
        drawable.setBounds(attr.left, attr.top, attr.right, attr.bottom)
        drawable.draw(mCanvas)
    }*/
}