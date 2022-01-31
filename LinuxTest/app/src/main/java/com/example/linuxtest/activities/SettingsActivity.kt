package com.example.linuxtest.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import com.example.linuxtest.databinding.ActivitySettingsBinding
import com.example.linuxtest.storage.Prefs

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val serverHostname = binding.editTextServerHostName
        val serverName = binding.editTextServerUserName
        val serverPassword = binding.editTextServerPassword
        val hostname = binding.editTextHostname
        val password = binding.editTextPassword
        val userName = binding.editTextUsername
        val saveSettingBtn = binding.saveSetting

        val sharedPref = Prefs(this)
        // Populate EditText's with saved info
        hostname.setText(sharedPref.hostname)
        password.setText(sharedPref.password)
        userName.setText(sharedPref.username)
        serverName.setText(sharedPref.serverName)
        serverPassword.setText(sharedPref.serverPassword)
        serverHostname.setText(sharedPref.serverHostname)

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
            sharedPref.serverName = serverName.text.toString()
            sharedPref.serverPassword = serverPassword.text.toString()
            sharedPref.serverHostname = serverHostname.text.toString()

            finish()
        }
    }
}
