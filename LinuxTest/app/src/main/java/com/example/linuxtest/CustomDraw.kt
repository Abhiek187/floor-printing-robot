package com.example.linuxtest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

class CustomDraw (context: Context) : View(context), AdapterView.OnItemSelectedListener {
    private val access =context as MainActivity
    private val paint = Paint()
    private var path = arrayListOf(Path(),Path(),Path(),Path(),Path())
    private var xCoord = 0f
    private var yCoord = 0f
    private var curWidth = 8f
    private val widths = arrayListOf(8f,10f,12f,14f,16f,18f,20f)
    private val infoWidth = ArrayAdapter(context,android.R.layout.simple_list_item_1,widths)
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

        performClick()
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.isAntiAlias=true
        paint.style=Paint.Style.STROKE
        val spin1 = access.brushWidth
        spin1.adapter = infoWidth
        var size = spin1.selectedItem.toString()
        paint.strokeWidth=size.toFloat()
        spin1.onItemSelectedListener=this
        for(i in 0 until path.size){
            //val spin1 = access.brushWidth
            //spin1.adapter = infoWidth
            //val size = spin1.selectedItem.toString()
            println("width is $curWidth")
            //paint.strokeWidth=size.toFloat()
            paint.strokeWidth=curWidth
            canvas.drawPath(path[i],paint)
        }
        access.clear.setOnClickListener{
            for(i in 0 until path.size){
                path[i].reset()
            }
            invalidate()
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(p0: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //p0!!.getItemAtPosition(pos)
        curWidth=widths[pos]
        infoWidth.notifyDataSetChanged()


    }
}