package com.example.linuxtest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.activity_main.*

class CustomDraw (context: Context) : View(context) {
    private val access =context as MainActivity
    private val paint = Paint()
    private var path = arrayOf(Path(),Path(),Path(),Path(),Path())
    private var xCoord = 0f
    private var yCoord = 0f
    private var paints = arrayListOf<Paint>()
    private var finalPath = arrayListOf<Array<Path>>()
    private var sizePaint=-1
    /*init{
        paint.isAntiAlias=true
        paint.style=Paint.Style.STROKE
        paint.strokeWidth=access.curWidth
        paints.add(paint)
    }*/
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //return super.onTouchEvent(event)
        when(event.action and MotionEvent.ACTION_MASK){
            MotionEvent.ACTION_DOWN,MotionEvent.ACTION_POINTER_DOWN ->{
                val ids = event.actionIndex
                if(ids>4) return false
                xCoord = event.getX(ids)
                yCoord= event.getY(ids)
                finalPath[sizePaint][ids].moveTo(xCoord,yCoord)
                //pathWidth.add(access.curWidth)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                for(i in 0 until event.pointerCount){
                    xCoord = event.getX(i)
                    yCoord = event.getY(i)
                    finalPath[sizePaint][i].lineTo(xCoord,yCoord)
                }
                invalidate()
            }
            MotionEvent.ACTION_UP,MotionEvent.ACTION_POINTER_UP -> {
                //pathWidth.remove(access.curWidth)
                invalidate()
            }
        }

        performClick()
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for(k in 0 until finalPath.size) {
            for (i in 0 until path.size) {
                //paint.strokeWidth = access.curWidth
                canvas.drawPath(finalPath[k][i], paints[k])
            }
        }
        access.clear.setOnClickListener{
            finalPath.clear()
            paints.clear()
            sizePaint=-1
            upDatePaint(access.curWidth)
            invalidate()
        }
    }

    fun upDatePaint(width: Float){
        val painted = Paint()
        painted.style=Paint.Style.STROKE
        painted.isAntiAlias=true
        painted.strokeWidth=width
        paints.add(painted)
        sizePaint += 1
        val paths = arrayOf(Path(),Path(),Path(),Path(),Path())
        finalPath.add(paths)
    }

}