package com.example.linuxtest.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.example.linuxtest.R

class ImageAdapter(context: Context, private val images: Array<Int>) :
    ArrayAdapter<Int>(context, R.layout.support_simple_spinner_dropdown_item,images) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        //return super.getDropDownView(position, convertView, parent)
        return getImagePosition(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //return super.getView(position, convertView, parent)
        return getImagePosition(position)
    }

    private fun getImagePosition(position: Int): View {
        val imageView = ImageView(context)
        imageView.setBackgroundResource(images[position])
        imageView.layoutParams = AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        return imageView
    }
}