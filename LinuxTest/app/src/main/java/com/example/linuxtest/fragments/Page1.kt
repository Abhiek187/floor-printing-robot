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
import com.example.linuxtest.adapter.ImageAdapter
import com.example.linuxtest.databinding.FragmentPage1Binding
import com.example.linuxtest.databinding.StrokeImagesBinding

/**
 * A simple [Fragment] subclass.
 */
class Page1 : Fragment() {
    private lateinit var mContext: Context

    private val colNames = arrayListOf("Black",/*"Red","Orange","Yellow","Green","Blue","Purple",
        "Brown",*/"White")

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
        val views = arrayOf(strokeBinding.view1, strokeBinding.view2, strokeBinding.view3,
            strokeBinding.view4, strokeBinding.view5, strokeBinding.view6, strokeBinding.view7)
        val imageArray = views.map { view -> Bitmap.createBitmap(view.layoutParams.width,
            view.layoutParams.height, Bitmap.Config.RGB_565) }

        val pictureAdapter = ImageAdapter(mContext, imageArray)
        spinWidth.adapter = pictureAdapter

        val infoColors = ArrayAdapter(mContext,android.R.layout.simple_list_item_1,colNames)
        infoColors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinColors.adapter=infoColors

        return binding.root
    }
}
