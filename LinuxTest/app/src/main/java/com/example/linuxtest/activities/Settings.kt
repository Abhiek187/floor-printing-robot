package com.example.linuxtest.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.linuxtest.R
import com.example.linuxtest.storage.Prefs

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val hostname = findViewById<EditText>(R.id.editTextHostname)
        val password = findViewById<EditText>(R.id.editTextPassword)
        val userName = findViewById<EditText>(R.id.editTextUsername)
        val saveSettingBtn = findViewById<Button>(R.id.saveSetting)

        val sharedPref = Prefs(this)
        // Populate EditText's with saved info
        hostname.setText(sharedPref.hostname)
        password.setText(sharedPref.password)
        userName.setText(sharedPref.username)

        saveSettingBtn.setOnClickListener {
            sharedPref.hostname = hostname.text.toString()
            sharedPref.password = password.text.toString()
            sharedPref.username = userName.text.toString()

            //startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}
