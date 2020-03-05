package ru.cscenter.fingerpaint.synchronization

import android.app.Activity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.api.ApiChooseTask
import ru.cscenter.fingerpaint.authentication.AuthenticateController
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.network.FailHandler
import ru.cscenter.fingerpaint.network.NetworkController
import ru.cscenter.fingerpaint.network.SuccessHandler
import ru.cscenter.fingerpaint.ui.games.base.GameResult
import ru.cscenter.fingerpaint.ui.title.TitleActivity
import ru.cscenter.fingerpaint.ui.title.toActivity

abstract class SynchronizationState(
    protected var auth: AuthenticateController,
    protected val networkController: NetworkController
) {
    protected val api = networkController.api
    protected val db = MainApplication.dbController

    fun syncAll() = syncUsers { users -> users.forEach { syncStatistics(it.id) } }
    abstract fun login()
    abstract fun syncUsers(onSync: (List<User>) -> Unit = {})
    abstract fun syncStatistics(id: Int)
    abstract fun checkUserExists(id: Int?, activity: Activity)
    abstract fun addUser(name: String, onResult: ResultHandler)
    abstract fun deleteUser(user: User, activity: Activity, onResult: ResultHandler)
    abstract fun updateUser(user: User, onResult: ResultHandler)
    abstract fun updateStatistic(statistic: Statistic, gameResult: GameResult)
    abstract fun loadChooseTasks(onSuccess: SuccessHandler<List<ApiChooseTask>>, onFail: FailHandler)

    fun setActivity(activity: Activity) {
        auth = AuthenticateController(activity)
    }

    fun logout(activity: Activity) = auth.logout { success ->
        if (success) {
            GlobalScope.launch(Dispatchers.IO) {
                db.clear()
            }
            toActivity(activity, TitleActivity::class.java)
        }
    }

    fun setIdToken(idToken: String) {
        networkController.idToken = idToken
        login()
    }
}
