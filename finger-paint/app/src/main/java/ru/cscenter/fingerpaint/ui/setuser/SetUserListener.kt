package ru.cscenter.fingerpaint.ui.setuser

interface SetUserListener {
    fun onSetUser(type: SetUserType, userId: Int)
}