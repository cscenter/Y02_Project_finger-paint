package ru.cscenter.fingerpaint.synchronization

import android.app.Activity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.api.ApiChooseTask
import ru.cscenter.fingerpaint.api.ApiPatient
import ru.cscenter.fingerpaint.api.ApiPatientName
import ru.cscenter.fingerpaint.authentication.AuthenticateController
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.network.*
import ru.cscenter.fingerpaint.ui.games.base.GameResult
import ru.cscenter.fingerpaint.ui.games.base.toInt
import ru.cscenter.fingerpaint.ui.title.TitleChooseUserActivity
import ru.cscenter.fingerpaint.ui.title.toActivity

class OnlineState(
    auth: AuthenticateController,
    networkController: NetworkController = NetworkController()
) : SynchronizationState(auth, networkController) {

    private fun onSyncFailed(
        onResult: ResultHandler = { },
        onStateChanged: (SynchronizationState) -> Unit = {}
    ) = { code: Int ->
        if (codeIsError(code)) {
            if (MainApplication.synchronizeController.state === this) {
                synchronized(this) {
                    if (MainApplication.synchronizeController.state === this) {
                        val newState = OfflineState(auth, networkController)
                        MainApplication.synchronizeController.state = newState
                    }
                }
            }
            onStateChanged(MainApplication.synchronizeController.state)
        } else {
            onResult(false)
        }
    }

    override fun login() = api.login().executeAsync({}, onSyncFailed())

    override fun syncUsers(onSync: (List<User>) -> Unit) =
        api.getPatients().executeAsync({ apiPatients ->
            GlobalScope.launch(Dispatchers.IO) {
                val users = apiPatients.map { it.toUser() }
                db.syncUsers(users)
                onSync(users)
            }
        }, onSyncFailed())


    override fun syncStatistics(id: Int) =
        api.getStatistics(id.toLong()).executeAsync({ apiStatistics ->
            GlobalScope.launch(Dispatchers.IO) {
                val statistics = apiStatistics.map { it.toStatistic(id) }.toTypedArray()
                db.setStatistics(*statistics)
            }
        }, onSyncFailed())

    override fun checkUserExists(id: Int?, activity: Activity) {
        fun onUserNotExists() = toActivity(activity, TitleChooseUserActivity::class.java)

        if (id == null) {
            onUserNotExists()
            return
        }
        api.checkPatientExists(id.toLong()).executeAsync({ exists ->
            if (!exists) {
                onUserNotExists()
            }
        }, onSyncFailed())
    }

    override fun addUser(name: String, onResult: ResultHandler) =
        api.addPatients(listOf(ApiPatientName(name))).executeAsync({ apiPatients ->
            GlobalScope.launch(Dispatchers.IO) {
                val patients = apiPatients.map { it.toUser() }
                db.insertUsers(patients)
                onResult(true)
            }
        }, onSyncFailed(onResult) { state ->
            state.addUser(name, onResult)
        })

    override fun deleteUser(user: User, activity: Activity, onResult: ResultHandler) =
        api.deletePatient(user.id.toLong()).executeAsync({
            GlobalScope.launch(Dispatchers.IO) {
                db.deleteUser(user)
                if (!db.hasCurrentUser()) {
                    toActivity(activity, TitleChooseUserActivity::class.java)
                }
                onResult(true)
            }
        }, onSyncFailed(onResult) { state ->
            state.deleteUser(user, activity, onResult)
        })

    override fun updateUser(user: User, onResult: ResultHandler) =
        api.updatePatients(listOf(ApiPatient(user.id.toLong(), user.name))).executeAsync({
            GlobalScope.launch(Dispatchers.IO) {
                db.setUser(user)
                onResult(true)
            }
        }, onSyncFailed(onResult) { state ->
            state.updateUser(user, onResult)
        })

    override fun updateStatistic(statistic: Statistic, gameResult: GameResult) {
        val apiGameResult = statisticToApiResult(statistic, gameResult)
        api.putStatistics(listOf(apiGameResult)).executeAsync({
            statistic.total++
            statistic.success += gameResult.toInt()
            GlobalScope.launch(Dispatchers.IO) {
                db.setStatistics(statistic)
            }
        }, onSyncFailed { state ->
            state.updateStatistic(statistic, gameResult)
        })
    }

    override fun loadChooseTasks(onSuccess: SuccessHandler<List<ApiChooseTask>>, onFail: FailHandler) =
        api.getChooseTasks().executeAsync(onSuccess, onFail)

}
