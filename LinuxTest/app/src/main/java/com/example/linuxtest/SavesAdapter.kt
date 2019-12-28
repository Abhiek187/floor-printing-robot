package com.example.linuxtest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_saves.view.*

class SavesAdapter(private var context: Context, private var saves: ArrayList<Image>):
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context).inflate(R.layout.adapter_saves, parent,
            false)

        return ViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewName.text = saves[position].name
        holder.textViewImage.text = saves[position].image

        holder.itemView.setOnClickListener {
            (context as SavesActivity).finishActivity(saves[position].name)
        }
    }

    override fun getItemCount(): Int {
        return saves.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    // Get all properties from adapter_saves
    val textViewName: TextView = view.textViewName
    val textViewImage: TextView = view.textViewImage
}
