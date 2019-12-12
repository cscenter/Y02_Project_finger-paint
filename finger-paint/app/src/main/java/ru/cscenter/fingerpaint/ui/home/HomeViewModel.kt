package ru.cscenter.fingerpaint.ui.home

import androidx.lifecycle.ViewModel
import ru.cscenter.fingerpaint.MainApplication

class HomeViewModel : ViewModel() {
    fun currentName() = MainApplication.dbController.currentUser?.toString() ?: ""
    fun currentId() = MainApplication.dbController.currentUser?.id ?: -1
}
