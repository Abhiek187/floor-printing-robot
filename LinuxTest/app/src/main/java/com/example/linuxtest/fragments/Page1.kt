package com.example.linuxtest.fragments

import android.content.Context
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

/**
 * A simple [Fragment] subclass.
 */
class Page1 : Fragment() {
    private lateinit var mContext: Context

    //private val widths = arrayListOf(8f,10f,12f,14f,16f,18f,20f)
    private val colNames = arrayListOf("Black",/*"Red","Orange","Yellow","Green","Blue","Purple",
        "Brown",*/"White")
    private val imageArrays = arrayOf(
        R.drawable.stroke_width_8f,
        R.drawable.stroke_width_10f,
        R.drawable.stroke_width_12f,
        R.drawable.stroke_width_14f,
        R.drawable.stroke_width_16f,
        R.drawable.stroke_width_18f,
        R.drawable.stroke_width_20f
    )

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

        /*val infoWidth = ArrayAdapter(mContext,android.R.layout.simple_list_item_1,widths)
        infoWidth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinWidth.adapter=infoWidth*/

        val pictureAdapter = ImageAdapter(mContext, imageArrays)
        spinWidth.adapter = pictureAdapter

        val infoColors = ArrayAdapter(mContext,android.R.layout.simple_list_item_1,colNames)
        infoColors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinColors.adapter=infoColors

        return binding.root
    }
}
