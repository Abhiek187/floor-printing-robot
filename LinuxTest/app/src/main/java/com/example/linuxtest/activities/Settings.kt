package com.example.linuxtest.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import com.example.linuxtest.R
import com.example.linuxtest.storage.Prefs

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val hostname = findViewById<EditText>(R.id.name)
        val password = findViewById<EditText>(R.id.Password)
        val userName = findViewById<EditText>(R.id.userID)
        val saveSettingBtn = findViewById<Button>(R.id.saveSetting)
        val sharedPref = Prefs(this)

        hostname.setOnKeyListener{_, keyCode,keyEvent ->
            if(keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                saveSettingBtn.performClick()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        saveSettingBtn.setOnClickListener {
            if (hostname.text.isNotEmpty()) {
                sharedPref.hostname = hostname.text.toString()
            }

            if (password.text.isNotEmpty()) {
                sharedPref.password = password.text.toString()
            }

            if (userName.text.isNotEmpty()) {
                sharedPref.username = userName.text.toString()
            }

            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}
