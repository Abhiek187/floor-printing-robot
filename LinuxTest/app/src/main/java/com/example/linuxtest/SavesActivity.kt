package com.example.linuxtest

import android.content.Intent
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
        val imagesDB = ImagesDBHelper(this)
        val saves = imagesDB.getSaves()

        recyclerViewSaves.layoutManager = LinearLayoutManager(this)
        val savesAdapter = SavesAdapter(this, saves)
        recyclerViewSaves.adapter = savesAdapter
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerViewSaves.addItemDecoration(divider)
    }

    fun finishActivity(name: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("imageName", name)
        startActivity(intent)
        finish() // can only finish() inside activity
    }
}
