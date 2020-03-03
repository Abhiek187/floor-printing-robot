package com.example.linuxtest.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import com.example.linuxtest.databinding.ActivitySettingsBinding
import com.example.linuxtest.storage.Prefs

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val hostname = binding.editTextHostname
        val password = binding.editTextPassword
        val userName = binding.editTextUsername
        val saveSettingBtn = binding.saveSetting

        val sharedPref = Prefs(this)
        // Populate EditText's with saved info
        hostname.setText(sharedPref.hostname)
        password.setText(sharedPref.password)
        userName.setText(sharedPref.username)

        hostname.setOnKeyListener{_, keyCode,keyEvent ->
            if(keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                saveSettingBtn.performClick()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        saveSettingBtn.setOnClickListener {
            sharedPref.hostname = hostname.text.toString()
            sharedPref.password = password.text.toString()
            sharedPref.username = userName.text.toString()

            //startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}
