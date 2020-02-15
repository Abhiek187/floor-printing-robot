package com.example.linuxtest.fragments

import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.linuxtest.R
import kotlin.properties.Delegates


/**
 * A simple [Fragment] subclass.
 */
class Page2 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_page2, container, false)
        val constraintLayout = view.findViewById(R.id.page2) as ConstraintLayout
        constraintLayout.addView(Demo(activity))
        return view
        //return inflater.inflate(R.layout.fragment_page2, container, false)
    }
}

class Demo(context: FragmentActivity?) : View(context){
    private var path: Path = Path()
    private var paint: Paint = Paint()
    private val framePerSec = 6
    private val duration: Long = 6000
    private var startTime by Delegates.notNull<Long>()
    private val xCoord = (this.resources.displayMetrics.widthPixels/3).toFloat()
    private val yCoord = (this.resources.displayMetrics.heightPixels/3).toFloat()


    init {
        this.startTime = System.currentTimeMillis()
        paint.color = Color.BLACK
        paint.isAntiAlias
        paint=Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth=14f
        /*path.moveTo(xCoord,yCoord)
        path.lineTo(xCoord+10f,yCoord)
        path.lineTo(xCoord+20f,yCoord)
        path.lineTo(xCoord+20f,yCoord+10f)
        path.lineTo(xCoord+10f,yCoord+10f)
        path.lineTo(xCoord,yCoord+10f)*/
        this.invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        path.moveTo(xCoord,yCoord)
        path.lineTo(xCoord+20f,yCoord)
        val currentTime: Long = System.currentTimeMillis() - startTime
        path.lineTo(xCoord+(10*currentTime/100).toFloat(),yCoord)
        canvas!!.drawPath(path,paint)
        if(currentTime < duration){
            this.postInvalidateDelayed((1000 / framePerSec).toLong())
        }

    }
}





