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

/**
 * A simple [Fragment] subclass.
 */
class Page1 : Fragment() {
    private lateinit var mContext: Context

    private val widths = arrayListOf(8f,10f,12f,14f,16f,18f,20f)
    private val colNames = arrayListOf("Black","Red","Orange","Yellow","Green","Blue","Purple",
        "Brown","White")

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
        val view = inflater.inflate(R.layout.fragment_page1,container,false)
        spinColors = view.findViewById(R.id.demoColors)
        spinWidth = view.findViewById(R.id.demoBrushWidth)

        val infoWidth = ArrayAdapter(mContext,android.R.layout.simple_list_item_1,widths)
        infoWidth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinWidth.adapter=infoWidth

        val infoColors = ArrayAdapter(mContext,android.R.layout.simple_list_item_1,colNames)
        infoColors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinColors.adapter=infoColors

        return view
    }
}
