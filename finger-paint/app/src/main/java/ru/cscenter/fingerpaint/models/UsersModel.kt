package ru.cscenter.fingerpaint.models

import android.app.Activity
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.ui.title.toTitleChooseUserActivity


class UsersModel : ViewModel() {
    val users = MainApplication.dbController.users

    suspend fun deleteUser(user: User, activity: Activity) = withContext(Dispatchers.IO) {
        MainApplication.dbController.deleteUser(user)
        if (!MainApplication.dbController.hasCurrentUser()) {
            toTitleChooseUserActivity(activity)
        }
    }

    suspend fun getUser(id: Int) =
        withContext(Dispatchers.IO) { MainApplication.dbController.getUser(id) }

    suspend fun insertUser(name: String) =
        withContext(Dispatchers.IO) { MainApplication.dbController.insertUser(name) }

    suspend fun updateUser(user: User) =
        withContext(Dispatchers.IO) { MainApplication.dbController.setUser(user) }
}
