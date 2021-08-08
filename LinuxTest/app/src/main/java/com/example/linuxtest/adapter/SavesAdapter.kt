package com.example.linuxtest.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.linuxtest.image.Image
import com.example.linuxtest.activities.SavesActivity
import com.example.linuxtest.databinding.AdapterSavesBinding

class SavesAdapter(private var context: Context, private var saves: List<Image>):
    RecyclerView.Adapter<SavesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = AdapterSavesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textViewName.text = saves[position].name
        val path = "${context.filesDir.path}/${saves[position].image}"
        val bitmap = BitmapFactory.decodeFile(path)
        holder.binding.textViewImage.setImageBitmap(bitmap)
        holder.binding.textViewImage.contentDescription = "Image of ${saves[position].name}"

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

    inner class ViewHolder(val binding: AdapterSavesBinding): RecyclerView.ViewHolder(binding.root) {
        // Get all properties from adapter_saves
    }
}
