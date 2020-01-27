package com.example.linuxtest

import android.content.Context
import android.content.SharedPreferences

class Prefs (context: Context) {
    private val fileName = "MyPrefs"
    private val isFirstKey = "isFirst"
    private val prefs: SharedPreferences = context.getSharedPreferences(fileName,
        Context.MODE_PRIVATE)

    var isFirst: Boolean
        get() = prefs.getBoolean(isFirstKey,true)
        set(value) =prefs.edit().putBoolean(isFirstKey,value).apply()
}