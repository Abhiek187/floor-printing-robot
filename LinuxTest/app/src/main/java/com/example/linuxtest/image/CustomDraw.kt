package com.example.linuxtest.image

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import com.example.linuxtest.activities.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread
import kotlin.math.roundToInt

class CustomDraw (context: Context) : View(context) {
    private val access = context as MainActivity
    private var xCoord = 0f
    private var yCoord = 0f
    private var mBitmap: Bitmap? = null // loaded drawing

    private var paints = arrayListOf<Paint>()
    private var finalPath = arrayListOf<Array<Path>>() // arrays of 5 paths
    private var sizePaint = -1

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action and MotionEvent.ACTION_MASK){
            MotionEvent.ACTION_DOWN,MotionEvent.ACTION_POINTER_DOWN -> {
                val ids = event.actionIndex
                if (ids > 4) return false
                xCoord = event.getX(ids)
                yCoord = event.getY(ids)
                finalPath[sizePaint][ids].moveTo(xCoord,yCoord)
            }
            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    xCoord = event.getX(i)
                    yCoord = event.getY(i)
                    finalPath[sizePaint][i].lineTo(xCoord,yCoord)
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
        mBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        for(k in 0 until finalPath.size) {
            for (path in finalPath[k]) {
                canvas.drawPath(path, paints[k])
            }
        }
        access.clear.setOnClickListener {
            mBitmap = null
            finalPath.clear()
            paints.clear()
            sizePaint = -1
            updatePaint(access.curWidth,access.curColor)
            invalidate()
            access.newDrawing()
        }
    }

    fun saveDrawing(image: String, scale: Boolean) {
        // Save drawing as a bitmap and convert it to a PNG file
        var bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE) // make background white instead of transparent (black)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        draw(canvas)

        val file = if (scale) {
            bitmap = Bitmap.createScaledBitmap(bitmap, (this.width / 3f).roundToInt(),
                (this.height / 3f).roundToInt(), true)
            File("${access.filesDir.path}/scaled_$image")
        } else {
            File("${access.filesDir.path}/$image")
        }

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
        finalPath.clear()
        paints.clear()
        sizePaint = -1
        updatePaint(access.curWidth,access.curColor)
        mBitmap = BitmapFactory.decodeFile(path) // immutable bitmap
        invalidate()
    }

    fun updatePaint(width: Float, color: Int){
        val painted = Paint(Paint.ANTI_ALIAS_FLAG)
        painted.style=Paint.Style.STROKE
        painted.strokeWidth=width
        painted.color=color
        paints.add(painted)
        sizePaint += 1
        val paths = Array(5) { Path() }
        finalPath.add(paths)
    }

    fun loadPicture(temp: Bitmap){
        //Retrieve picture from Photo Gallery
        finalPath.clear()
        paints.clear()
        sizePaint = -1
        updatePaint(access.curWidth,access.curColor)
        mBitmap = Bitmap.createBitmap(temp)
        invalidate()
    }
}
