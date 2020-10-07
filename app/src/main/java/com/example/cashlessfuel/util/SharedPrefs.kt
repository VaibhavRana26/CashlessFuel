package com.example.cashlessfuel.util

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs  {

    private val APP_SETTINGS = "APP_SETTINGS"

    private val PIN = "PIN"
    private val ISLOGIN = "LOGIN"
    private val MOBILE = "MOBILE"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)
    }

    fun getPin(context: Context): String? {
        return getSharedPreferences(context).getString(PIN, "")
    }

    fun setPin(context: Context, newValue: String?) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(PIN, newValue)
        editor.apply()
    }

    fun getLogin(context: Context): Boolean? {
        return getSharedPreferences(context).getBoolean(ISLOGIN, false)
    }

    fun setLogin(context: Context, newValue: Boolean?) {
        val editor = getSharedPreferences(context).edit()
        editor.putBoolean(ISLOGIN, newValue!!)
        editor.apply()
    }
    fun getPhone(context: Context): String? {
        return getSharedPreferences(context).getString(MOBILE, "")
    }

    fun setPhone(context: Context, newValue: String?) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(MOBILE, newValue)
        editor.apply()
    }



}