package ru.cscenter.fingerpaint

import androidx.appcompat.app.AppCompatActivity
import ru.cscenter.fingerpaint.ui.setuser.SetUserListener

abstract class ActivitySetUserListenerContainer : AppCompatActivity() {
    private var listener: SetUserListener? = null

    fun getSetUserListener() = listener

    fun setSetUserListener(listener: SetUserListener?) {
        this.listener = listener
    }
}