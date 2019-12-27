package com.example.linuxtest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class CustomDraw (context: Context) : View(context){
    private val access =context as MainActivity
    private val paint = Paint()
    private var path = arrayListOf(Path(),Path(),Path(),Path(),Path())
    private var xCoord = 0f
    private var yCoord = 0f
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //return super.onTouchEvent(event)
        when(event.action and MotionEvent.ACTION_MASK){
            MotionEvent.ACTION_DOWN,MotionEvent.ACTION_POINTER_DOWN ->{
                val ids = event.actionIndex
                if(ids>4) return false
                xCoord = event.getX(ids)
                yCoord= event.getY(ids)
                path[ids].moveTo(xCoord,yCoord)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                for(i in 0 until event.pointerCount){
                    xCoord = event.getX(i)
                    yCoord = event.getY(i)
                    path[i].lineTo(xCoord,yCoord)
                }
                invalidate()
            }
            MotionEvent.ACTION_UP,MotionEvent.ACTION_POINTER_UP -> {
                invalidate()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.isAntiAlias=true
        paint.style=Paint.Style.STROKE
        for(i in 0 until path.size){
            canvas.drawPath(path[i],paint)
        }

        access.clear.setOnClickListener{
            for(i in 0 until path.size){
                path[i].reset()
            }
            invalidate()
        }
    }
}