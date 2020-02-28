package com.example.linuxtest.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import com.example.linuxtest.R

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
        val view = inflater.inflate(R.layout.fragment_page1,container,false)
        spinColors = view.findViewById(R.id.demoColors)
        spinWidth = view.findViewById(R.id.demoBrushWidth)

        /*val infoWidth = ArrayAdapter(mContext,android.R.layout.simple_list_item_1,widths)
        infoWidth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinWidth.adapter=infoWidth*/

        val pictureAdapter = ImageAdapter(mContext, imageArrays)
        spinWidth.adapter = pictureAdapter

        val infoColors = ArrayAdapter(mContext,android.R.layout.simple_list_item_1,colNames)
        infoColors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinColors.adapter=infoColors

        return view
    }
}

class ImageAdapter(context: Context, private val images: Array<Int>) :
    ArrayAdapter<Int>(context,R.layout.support_simple_spinner_dropdown_item,images){

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        //return super.getDropDownView(position, convertView, parent)
        return getImagePosition(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //return super.getView(position, convertView, parent)
        return getImagePosition(position)
    }

    private fun getImagePosition(position: Int): View{
        val imageView = ImageView(context)
        imageView.setBackgroundResource(images[position])
        imageView.layoutParams =
            AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return imageView
    }
}
