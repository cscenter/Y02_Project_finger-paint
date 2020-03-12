package ru.cscenter.fingerpaint.ui.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.synchronization.OnlineState

class Settings(context: Context) {

    private val preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
    private val isOnline = MutableLiveData<Boolean>(isOnlineState())

    fun setVibrate(vibrate: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(VIBRATE, vibrate)
        editor.apply()
    }

    fun getVibrate() = preferences.getBoolean(VIBRATE, true)

    fun updateState() {
        val editor = preferences.edit()
        val state = MainApplication.synchronizeController.state is OnlineState
        editor.putBoolean(STATE, state)
        isOnline.postValue(state)
        editor.apply()
    }

    fun isOnlineState() = preferences.getBoolean(STATE, true)

    fun isOnline(): LiveData<Boolean> = isOnline

    companion object {
        private const val SETTINGS = "settings"
        private const val VIBRATE = "vibrate"
        private const val STATE = "state"
    }

}
