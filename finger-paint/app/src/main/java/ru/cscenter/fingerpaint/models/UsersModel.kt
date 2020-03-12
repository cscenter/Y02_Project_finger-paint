package ru.cscenter.fingerpaint.models

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.cscenter.fingerpaint.MainApplication


class UsersModel : ViewModel() {
    val users = MainApplication.dbController.users

    suspend fun getUser(id: Int) =
        withContext(Dispatchers.IO) { MainApplication.dbController.getUser(id) }
}
