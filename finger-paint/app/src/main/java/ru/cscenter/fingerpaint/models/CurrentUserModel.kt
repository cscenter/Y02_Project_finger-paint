package ru.cscenter.fingerpaint.models

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.db.User

class CurrentUserModel : ViewModel() {
    val currentUser = MainApplication.dbController.currentUser

    suspend fun setCurrentUser(user: User) =
        withContext(Dispatchers.IO) { MainApplication.dbController.setCurrentUser(user.id) }

    suspend fun hasCurrentUser() =
        withContext(Dispatchers.IO) { MainApplication.dbController.hasCurrentUser() }
}
