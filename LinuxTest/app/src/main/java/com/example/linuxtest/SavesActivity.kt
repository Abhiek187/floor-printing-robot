package com.example.linuxtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SavesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saves)

        val recyclerViewSaves = findViewById<RecyclerView>(R.id.recyclerViewSaves)
        val saves = arrayListOf("Image 1", "Image 2", "Image 3")

        recyclerViewSaves.layoutManager = LinearLayoutManager(this)
        val savesAdapter = SavesAdapter(this, saves)
        recyclerViewSaves.adapter = savesAdapter
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerViewSaves.addItemDecoration(divider)
    }
}
