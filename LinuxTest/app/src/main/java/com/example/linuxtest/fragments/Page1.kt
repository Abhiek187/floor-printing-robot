package com.example.linuxtest.fragments

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.linuxtest.R
import com.example.linuxtest.adapter.ImageAdapter
import com.example.linuxtest.databinding.FragmentPage1Binding
import com.example.linuxtest.databinding.StrokeImagesBinding

/**
 * A simple [Fragment] subclass.
 */
class Page1 : Fragment() {
    private lateinit var mContext: Context

    //private val widths = arrayListOf(8f,10f,12f,14f,16f,18f,20f)
    private val colNames = arrayListOf("Black",/*"Red","Orange","Yellow","Green","Blue","Purple",
        "Brown",*/"White")
    /*private val imageArrays = arrayOf(
        R.drawable.stroke_width_8f,
        R.drawable.stroke_width_10f,
        R.drawable.stroke_width_12f,
        R.drawable.stroke_width_14f,
        R.drawable.stroke_width_16f,
        R.drawable.stroke_width_18f,
        R.drawable.stroke_width_20f
    )*/

    private lateinit var spinColors: Spinner
    private lateinit var spinWidth: Spinner

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentPage1Binding.inflate(inflater, container, false)
        spinColors = binding.demoColors
        spinWidth = binding.demoBrushWidth

        val strokeBinding = StrokeImagesBinding.inflate(layoutInflater)
        val view1 = strokeBinding.view1
        val view2 = strokeBinding.view2
        val view3 = strokeBinding.view3
        val view4 = strokeBinding.view4
        val view5 = strokeBinding.view5
        val view6 = strokeBinding.view6
        val view7 = strokeBinding.view7

        val b1 = Bitmap.createBitmap(view1.layoutParams.width,view1.layoutParams.height, Bitmap.Config.RGB_565)
        val b2 = Bitmap.createBitmap(view2.layoutParams.width,view2.layoutParams.height, Bitmap.Config.RGB_565)
        val b3 = Bitmap.createBitmap(view3.layoutParams.width,view3.layoutParams.height, Bitmap.Config.RGB_565)
        val b4 = Bitmap.createBitmap(view4.layoutParams.width,view4.layoutParams.height, Bitmap.Config.RGB_565)
        val b5 = Bitmap.createBitmap(view5.layoutParams.width,view5.layoutParams.height, Bitmap.Config.RGB_565)
        val b6 = Bitmap.createBitmap(view6.layoutParams.width,view6.layoutParams.height, Bitmap.Config.RGB_565)
        val b7 = Bitmap.createBitmap(view7.layoutParams.width,view7.layoutParams.height, Bitmap.Config.RGB_565)

        val imageArray = arrayOf(b1,b2,b3,b4,b5,b6,b7)
        /*val infoWidth = ArrayAdapter(mContext,android.R.layout.simple_list_item_1,widths)
        infoWidth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinWidth.adapter=infoWidth*/

        val pictureAdapter = ImageAdapter(mContext, imageArray)
        spinWidth.adapter = pictureAdapter

        val infoColors = ArrayAdapter(mContext,android.R.layout.simple_list_item_1,colNames)
        infoColors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinColors.adapter=infoColors

        return binding.root
    }
}
