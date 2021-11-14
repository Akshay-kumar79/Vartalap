package com.example.vartalap.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE)


    fun putBoolean(key: String, value: Boolean) =  sharedPreferences.edit().putBoolean(key, value).apply()
    fun getBoolean(key: String): Boolean = sharedPreferences.getBoolean(key, false)

    fun putString(key: String, value: String) = sharedPreferences.edit().putString(key,value).apply()
    fun getString(key: String): String? = sharedPreferences.getString(key, "")

    fun clear() = sharedPreferences.edit().clear().apply()
}