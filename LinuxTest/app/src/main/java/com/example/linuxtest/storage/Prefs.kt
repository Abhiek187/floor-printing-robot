package com.example.linuxtest.storage

import android.content.Context
import android.content.SharedPreferences

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
        set(value) = prefs.edit().putBoolean(isFirstKey,value).apply()

    var username: String
        get() = prefs.getString(userKey,"").toString()
        set(value) = prefs.edit().putString(userKey,value).apply()

    var password: String
        get() = prefs.getString(passwordKey,"").toString()
        set(value) = prefs.edit().putString(passwordKey,value).apply()

    var hostname: String
        get() = prefs.getString(hostNameKey,"").toString()
        set(value) = prefs.edit().putString(hostNameKey,value).apply()

    var serverName: String
        get() = prefs.getString(serverNameKey,"").toString()
        set(value) = prefs.edit().putString(serverNameKey,value).apply()

    var serverPassword: String
        get() = prefs.getString(serverPasswordKey,"").toString()
        set(value) = prefs.edit().putString(serverPasswordKey,value).apply()

    var serverHostname: String
        get() = prefs.getString(serverHostnameKey,"").toString()
        set(value) = prefs.edit().putString(serverHostnameKey,value).apply()
}
