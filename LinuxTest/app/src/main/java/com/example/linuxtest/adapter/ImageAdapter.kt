package com.example.linuxtest.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.linuxtest.databinding.ImagelistLayoutBinding

class ImageAdapter(context: Context, private val images: List<Bitmap>) :
    ArrayAdapter<Bitmap>(context,android.R.layout.simple_spinner_dropdown_item,images) {

    private val mInflater = LayoutInflater.from(context)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ImagelistLayoutBinding.inflate(mInflater, parent, false)
        val image = binding.imageHolder
        //image.setImageResource(images[position])
        image.setImageBitmap(images[position])
        return image
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ImagelistLayoutBinding.inflate(mInflater, parent, false)
        val image = binding.imageHolder
        //image.setImageResource(images[position])
        image.setImageBitmap(images[position])
        return image
    }
}