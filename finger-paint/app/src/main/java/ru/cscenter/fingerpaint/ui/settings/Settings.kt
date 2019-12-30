package ru.cscenter.fingerpaint.ui.settings

import android.content.Context

class Settings(context: Context) {

    private val preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)

    fun setVibrate(vibrate: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(VIBRATE, vibrate)
        editor.apply()
    }

    fun getVibrate() = preferences.getBoolean(VIBRATE, true)

    companion object {
        private const val SETTINGS = "settings"
        private const val VIBRATE = "vibrate"
    }

}