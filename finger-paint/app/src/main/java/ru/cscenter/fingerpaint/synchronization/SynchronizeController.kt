package ru.cscenter.fingerpaint.synchronization

import android.app.Activity
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.authentication.AuthenticateController
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.ui.games.base.GameResult

typealias ResultHandler = (success: Boolean) -> Unit

class SynchronizeController(auth: AuthenticateController) {
    var state: SynchronizationState =
        if (MainApplication.settings.isOnlineState()) OnlineState(auth) else OfflineState(auth)
        set(value) {
            field = value
            MainApplication.settings.updateState()
        }
    var info: (String) -> Unit = {}

    fun logout(activity: Activity) = state.logout(activity)
    fun syncAll() = state.syncAll()
    fun syncUsers(onSync: (List<User>) -> Unit = {}) = state.syncUsers(onSync)
    fun syncStatistics(id: Int) = state.syncStatistics(id)
    fun checkUserExists(id: Int?, activity: Activity) = state.checkUserExists(id, activity)
    fun addUser(name: String, onResult: ResultHandler) = state.addUser(name, onResult)
    fun updateUser(user: User, onResult: ResultHandler) = state.updateUser(user, onResult)
    fun deleteUser(user: User, activity: Activity, onResult: ResultHandler) =
        state.deleteUser(user, activity, onResult)

    fun updateStatistic(statistic: Statistic, gameResult: GameResult) =
        state.updateStatistic(statistic, gameResult)

    fun setIdToken(idToken: String) = state.setIdToken(idToken)
    fun setActivity(activity: Activity) = state.setActivity(activity)
}
