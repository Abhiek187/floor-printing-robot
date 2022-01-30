package com.example.linuxtest.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.linuxtest.adapter.SavesAdapter
import com.example.linuxtest.databinding.ActivitySavesBinding
import com.example.linuxtest.storage.ImageDatabase

class SavesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySavesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerViewSaves = binding.recyclerViewSaves
        val imagesDB = ImageDatabase.getInstance(this).imageDao()

        // Retrieve all the saves in the background and populate the recycler view
        imagesDB.getSaves().observe(this) { saves ->
            val savesAdapter = SavesAdapter(this, saves)
            recyclerViewSaves.adapter = savesAdapter
        }

        recyclerViewSaves.layoutManager = LinearLayoutManager(this)
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerViewSaves.addItemDecoration(divider)
    }

    fun finishActivity(name: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("imageName", name)
        setResult(Activity.RESULT_OK, intent)
        finish() // can only finish() inside activity
    }
}
