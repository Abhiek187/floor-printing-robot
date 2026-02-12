package com.example.linuxtest.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class Prefs (context: Context) {
    private val fileName = "MyPrefs"
    private val isFirstKey = "isFirst"
    private val userKey = "username"
    private val passwordKey = "password"
    private val hostNameKey = "hostname"
    private val serverNameKey = "serverName"
    private val serverPasswordKey = "serverPassword"
    private val serverHostnameKey = "serverHostname"
    private val prefs: SharedPreferences = context.getSharedPreferences(fileName,
        Context.MODE_PRIVATE)

    var isFirst: Boolean
        get() = prefs.getBoolean(isFirstKey,true)
        set(value) = prefs.edit { putBoolean(isFirstKey, value) }

    var username: String
        get() = prefs.getString(userKey,"")!!
        set(value) = prefs.edit { putString(userKey, value) }

    var password: String
        get() = prefs.getString(passwordKey,"")!!
        set(value) = prefs.edit { putString(passwordKey, value) }

    var hostname: String
        get() = prefs.getString(hostNameKey,"")!!
        set(value) = prefs.edit { putString(hostNameKey, value) }

    var serverName: String
        get() = prefs.getString(serverNameKey,"")!!
        set(value) = prefs.edit { putString(serverNameKey, value) }

    var serverPassword: String
        get() = prefs.getString(serverPasswordKey,"")!!
        set(value) = prefs.edit { putString(serverPasswordKey, value) }

    var serverHostname: String
        get() = prefs.getString(serverHostnameKey,"")!!
        set(value) = prefs.edit { putString(serverHostnameKey, value) }
}
