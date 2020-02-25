package ru.cscenter.fingerpaint.synchronization

import android.app.Activity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.api.ApiPatient
import ru.cscenter.fingerpaint.api.ApiPatientName
import ru.cscenter.fingerpaint.authentication.AuthenticateController
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.network.*
import ru.cscenter.fingerpaint.ui.games.base.GameResult
import ru.cscenter.fingerpaint.ui.games.base.toInt
import ru.cscenter.fingerpaint.ui.title.TitleActivity
import ru.cscenter.fingerpaint.ui.title.TitleChooseUserActivity
import ru.cscenter.fingerpaint.ui.title.toActivity

typealias ResultHandler = (success: Boolean) -> Unit

class SynchronizeController {
    private val networkController = NetworkController()
    private val api = networkController.api
    private val db = MainApplication.dbController
    private lateinit var auth: AuthenticateController

    private fun <T> onSyncFailed(onResult: ResultHandler = { }): FailHandler<T> =
        { call, onSuccess, code ->
            onResult(false)
        }

    private fun login() = api.login().executeAsync({}, onSyncFailed())

    fun logout(activity: Activity) {
        auth.logout { success ->
            if (success) {
                GlobalScope.launch(Dispatchers.IO) {
                    db.clear()
                }
                toActivity(activity, TitleActivity::class.java)
            }
        }
    }

    fun syncAll() = syncUsers { users -> users.forEach { syncStatistics(it.id) } }

    fun syncUsers(onSync: (List<User>) -> Unit = {}) =
        api.getPatients().executeAsync({ apiPatients ->
            GlobalScope.launch(Dispatchers.IO) {
                val users = apiPatients.map { it.toUser() }
                db.syncUsers(users)
                onSync(users)
            }
        }, onSyncFailed())


    fun syncStatistics(id: Int) = api.getStatistics(id.toLong()).executeAsync({ apiStatistics ->
        GlobalScope.launch(Dispatchers.IO) {
            val statistics = apiStatistics.map { it.toStatistic() }.toTypedArray()
            db.setStatistics(*statistics)
        }
    }, onSyncFailed())

    fun checkUserExists(id: Int?, activity: Activity) {
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

    fun addUser(name: String, onResult: ResultHandler) =
        api.addPatients(listOf(ApiPatientName(name))).executeAsync({ apiPatients ->
            GlobalScope.launch(Dispatchers.IO) {
                val patients = apiPatients.map { it.toUser() }
                db.insertUsers(patients)
                onResult(true)
            }
        }, onSyncFailed(onResult))

    fun deleteUser(user: User, activity: Activity, onResult: ResultHandler) =
        api.deletePatient(user.id.toLong()).executeAsync({
            GlobalScope.launch(Dispatchers.IO) {
                db.deleteUser(user)
                if (!db.hasCurrentUser()) {
                    toActivity(activity, TitleChooseUserActivity::class.java)
                }
                onResult(true)
            }
        }, onSyncFailed(onResult))

    fun updateUser(user: User, onResult: ResultHandler) =
        api.updatePatient(ApiPatient(user.id.toLong(), user.name)).executeAsync({
            GlobalScope.launch(Dispatchers.IO) {
                db.setUser(user)
                onResult(true)
            }
        }, onSyncFailed(onResult))

    fun updateStatistic(statistic: Statistic, gameResult: GameResult) {
        val apiGameResult = statisticToApi(statistic, gameResult)
        api.putStatistics(apiGameResult).executeAsync({}, onSyncFailed())
        statistic.total++
        statistic.success += gameResult.toInt()
        GlobalScope.launch(Dispatchers.IO) {
            db.setStatistics(statistic)
        }
    }

    fun setIdToken(idToken: String?) {
        networkController.idToken = idToken
        login()
    }

    fun setActivity(activity: Activity) {
        auth = AuthenticateController(activity)
    }

}