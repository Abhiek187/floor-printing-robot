package com.example.linuxtest.fragments

import android.annotation.SuppressLint
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

        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.page2)
        val boundary1 = view.findViewById<View>(R.id.boundary1)
        val boundary2 = view.findViewById<View>(R.id.boundary2)

        boundary2.post {
            // Add animation dynamically (assuming both boundaries are posted)
            constraintLayout.addView(Demo(mContext, boundary1, boundary2))
        }

        return view
    }
}

@SuppressLint("ViewConstructor")
class Demo(context: Context, boundary1: View, boundary2: View) : View(context) {
    // Paths: the horizontal lines + the vertical line (a parking lot)
    private var paths = Array(6) { Path() }
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val framePerSec = 15
    private var startTime = System.currentTimeMillis()
    // Our "canvas" (x, y) boundaries
    private val xMin = 0f
    private val xMax = this.resources.displayMetrics.widthPixels.toFloat()
    private val xDelta = xMax - xMin
    private val yMin = boundary1.y
    private val yMax = boundary2.y
    private val yDelta = yMax - yMin
    private var num = 0
    init {
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 14f
        invalidate() // start drawing the animation
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paths[0].moveTo(xMin, yMin + 0.1f*yDelta)
        paths[1].moveTo(xMin, yMin + 0.3f*yDelta)
        paths[2].moveTo(xMin, yMin + 0.5f*yDelta)
        paths[3].moveTo(xMin, yMin + 0.7f*yDelta)
        paths[4].moveTo(xMin, yMin + 0.9f*yDelta)
        paths[5].moveTo(xMin + 0.5f*xDelta, yMin)

        if(num==0) {
            val currentTime = System.currentTimeMillis() - startTime
            val xDest = 2f * currentTime
            if (xMin + xDest < xMax) {
                // Keep calling onDraw until lines reach the boundaries
                paths[num].lineTo(xMin + xDest, yMin + 0.1f * (num * 2 + 1) * yDelta)
                canvas.drawPath(paths[num], paint)
                postInvalidateDelayed(1000L / framePerSec)
            } else {
                // Stop the line at the boundary
                paths[num].lineTo(xMax, yMin + 0.1f * (num * 2 + 1) * yDelta)
                canvas.drawPath(paths[num], paint)
                num=1
                startTime = System.currentTimeMillis()
                invalidate()
            }
        }

        if(num in 1..4){ // Draw all horizontal lines
            for(i in 0 until num){ // Redraw all previous paths
                canvas.drawPath(paths[i],paint)
            }
            val currentTime = System.currentTimeMillis() - startTime
            val xDest = 2f * currentTime
            if (xMin + xDest < xMax) {
                // Keep calling onDraw until lines reach the boundaries
                paths[num].lineTo(xMin + xDest, yMin + 0.1f * (num * 2 + 1) * yDelta)
                canvas.drawPath(paths[num], paint)
                postInvalidateDelayed(1000L / framePerSec)
            } else {
                // Stop the line at the boundary
                paths[num].lineTo(xMax, yMin + 0.1f * (num * 2 + 1) * yDelta)
                canvas.drawPath(paths[num], paint)
                num += 1
                startTime = System.currentTimeMillis()
                invalidate()
            }

        }

        if(num==paths.size-1){ //Draw the last vertical path
            for(i in 0 until num){ // Redraw all previous paths
                canvas.drawPath(paths[i],paint)
            }
            val currentTime = System.currentTimeMillis() - startTime
            val yDest = 2f * currentTime
            if (yMin + yDest < yMax) {
                paths[num].lineTo(xMin + 0.5f*xDelta, yMin + yDest)
                canvas.drawPath(paths[num], paint)
                postInvalidateDelayed(1000L / framePerSec)
            } else {
                paths[num].lineTo(xMin + 0.5f*xDelta, yMax)
                canvas.drawPath(paths[num], paint)
            }
        }

        /*for(i in paths.indices) {
            // Keep adding to the path at the specified frame rate for a certain duration
            val currentTime = System.currentTimeMillis() - startTime
            val xDest = (53 * currentTime / 100).toFloat()
            val yDest = (64 * currentTime / 100).toFloat()

            // Keep making longer lines at each frame
            if (i < paths.size - 1) {
                if (xMin + xDest < xMax) {
                    // Keep calling onDraw until lines reach the boundaries
                    paths[i].lineTo(xMin + xDest, yMin + 0.1f * (i * 2 + 1) * yDelta)
                    canvas.drawPath(paths[i], paint)
                    postInvalidateDelayed(1000L / framePerSec)
                } else {
                    // Stop the line at the boundary
                    paths[i].lineTo(xMax, yMin + 0.1f * (i * 2 + 1) * yDelta)
                    canvas.drawPath(paths[i], paint)
                }
            } else {
                if (yMin + yDest < yMax) {
                    paths[i].lineTo(xMin + 0.5f*xDelta, yMin + yDest)
                    canvas.drawPath(paths[i], paint)
                    postInvalidateDelayed(1000L / framePerSec)
                } else {
                    paths[i].lineTo(xMin + 0.5f*xDelta, yMax)
                    canvas.drawPath(paths[i], paint)
                }
            }
        }*/
    }
}
