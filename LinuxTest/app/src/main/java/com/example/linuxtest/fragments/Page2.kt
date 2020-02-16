package com.example.linuxtest.fragments

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.linuxtest.R
import kotlin.properties.Delegates


/**
 * A simple [Fragment] subclass.
 */
class Page2 : Fragment() {
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_page2, container, false)
        val constraintLayout = view.findViewById(R.id.page2) as ConstraintLayout
        constraintLayout.addView(Demo(mContext))
        return view
        //return inflater.inflate(R.layout.fragment_page2, container, false)
    }
}

class Demo(context: Context) : View(context){
    private var paths = arrayListOf(Path(),Path(),Path(),Path(),Path())
    private var paint: Paint = Paint()
    private val framePerSec = 15
    private val duration: Long = 5000
    private var startTime by Delegates.notNull<Long>()
    private val xCoord = (this.resources.displayMetrics.widthPixels/3).toFloat()
    private val yCoord = (this.resources.displayMetrics.heightPixels/3).toFloat()

    init {

        startTime = System.currentTimeMillis()
        paint.color = Color.BLACK
        paint.isAntiAlias
        paint=Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth=14f
        invalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paths[0].moveTo((xCoord*0.6).toFloat(),yCoord)
        paths[1].moveTo((xCoord*0.6).toFloat(),yCoord+80f)
        paths[2].moveTo((xCoord*0.6).toFloat(),yCoord+160f)
        paths[3].moveTo((xCoord*0.6).toFloat(),yCoord+240f)
        paths[4].moveTo(xCoord,yCoord)

        paths[0].lineTo((xCoord*0.6).toFloat()+20f,yCoord)
        paths[1].lineTo((xCoord*0.6).toFloat()+20f,yCoord+80f)
        paths[2].lineTo((xCoord*0.6).toFloat()+20f,yCoord+160f)
        paths[3].lineTo((xCoord*0.6).toFloat()+20f,yCoord+240f)
        paths[4].lineTo(xCoord,yCoord+20f)

        for(i in paths.indices) {
                val currentTime: Long = System.currentTimeMillis() - startTime
                if (i < 4) {
                    paths[i].lineTo((xCoord*0.6).toFloat() + (8 * currentTime / 100).toFloat(),
                        yCoord + (80f * i))
                }
                else {
                    paths[i].lineTo(xCoord, yCoord + (6 * currentTime / 100).toFloat())
                }
                canvas?.drawPath(paths[i], paint)
                if (currentTime < duration) {
                    postInvalidateDelayed((1000 / framePerSec).toLong())
                }
        }

    }


}

