package ru.cscenter.fingerpaint.synchronization

import android.app.Activity
import android.os.Handler
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.api.ApiChooseTask
import ru.cscenter.fingerpaint.authentication.AuthenticateController
import ru.cscenter.fingerpaint.db.CachedGameResult
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.db.UserStatus
import ru.cscenter.fingerpaint.network.FailHandler
import ru.cscenter.fingerpaint.network.NetworkController
import ru.cscenter.fingerpaint.network.SuccessHandler
import ru.cscenter.fingerpaint.network.executeAsync
import ru.cscenter.fingerpaint.ui.games.base.GameResult
import ru.cscenter.fingerpaint.ui.games.base.toInt

class OfflineState(
    auth: AuthenticateController,
    networkController: NetworkController = NetworkController()
) : SynchronizationState(auth, networkController) {
    private val cache = RequestsCache(api, db)
    private var lastLogIn = 0L
    private var lastInfo = 0L
    private val handler = Handler()

    init {
        tryLogin()
    }

    override fun login() = onSignInSuccess()

    private fun tryLoginLater() = handler.postDelayed({ tryLogin() }, TRY_LOGIN_FREQUENCY_MS)

    private fun tryLogin(): Unit = api.login().executeAsync({
        onLoginSuccess()
    }, { code ->
        if (code != 401) {
            tryLoginLater()
            return@executeAsync
        }
        Log.d("fingerpaint", "Auth error!")
        silentSignIn()
    })

    private fun silentSignIn(): Unit = auth.silentLogin { success ->
        if (success) return@silentLogin // super will call login()
        tryLoginLater()
        Log.d("fingerpaint", "Silent sign in failed")
        if (System.currentTimeMillis() - lastLogIn > MILLIS_IN_HOUR) {
            lastLogIn = System.currentTimeMillis()
            auth.login()
        }
    }

    private fun loginOnce(onResult: (Int) -> Unit) =
        api.login().executeAsync({ onResult(200) }, { code -> onResult(code) })

    private fun onSignInSuccess() = loginOnce { code ->
        Log.d("fingerpaint", "Retry login succeed: $code")
        if (code == 200) {
            onLoginSuccess()
            return@loginOnce
        }
        tryLoginLater()
        if (code == 401 && System.currentTimeMillis() - lastInfo > INFO_CHANGE_TIME_FREQUENCY_MS) {
            lastInfo = System.currentTimeMillis()
            MainApplication.synchronizeController.info(
                "Проверьте правильность даты на устройстве. Из-за неправильной даты невозможно синхронизовать данные с сервером."
            )
        }
    }

    private fun onLoginSuccess() {
        Log.d("fingerpaint", "Login success! Start sync cache")
        cache.syncCache { success ->
            Log.d("fingerpaint", "Sync cache succeed: $success")
            if (!success) {
                tryLoginLater()
                return@syncCache
            }
            goOnline()
        }
    }

    private fun goOnline() = synchronized(this) {
        if (MainApplication.synchronizeController.state !== this) {
            Log.d("fingerpaint", "State had been already changed")
            return@synchronized
        }
        Log.d("fingerpaint", "Change to online state")
        val newState = OnlineState(auth, networkController)
        MainApplication.synchronizeController.state = newState
        newState.syncAll()
    }

    override fun syncUsers(onSync: (List<User>) -> Unit) {}
    override fun syncStatistics(id: Int) {}
    override fun checkUserExists(id: Int?, activity: Activity) {}
    override fun loadChooseTasks(onSuccess: SuccessHandler<List<ApiChooseTask>>, onFail: FailHandler) {}

    override fun addUser(name: String, onResult: ResultHandler) {
        GlobalScope.launch(Dispatchers.IO) {
            val result = db.insertUser(name, UserStatus.NEW)
            val success = result != -1
            withContext(Dispatchers.Main) { onResult(success) }
        }
    }

    // delete is prohibited while offline
    override fun deleteUser(user: User, activity: Activity, onResult: ResultHandler) {
        onResult(false)
    }

    override fun updateUser(user: User, onResult: ResultHandler) {
        GlobalScope.launch(Dispatchers.IO) {
            val status = if (user.status == UserStatus.NEW) UserStatus.NEW else UserStatus.UPDATED
            user.status = status
            val success = db.setUser(user)
            withContext(Dispatchers.Main) { onResult(success) }
        }
    }

    override fun updateStatistic(statistic: Statistic, gameResult: GameResult) {
        statistic.total++
        statistic.success += gameResult.toInt()
        GlobalScope.launch(Dispatchers.IO) {
            db.setStatistics(statistic)
            db.addGameResult(
                CachedGameResult(
                    statistic.userId,
                    statistic.date,
                    statistic.type,
                    gameResult == GameResult.SUCCESS
                )
            )
        }
    }

    companion object {
        const val MILLIS_IN_HOUR = 3_600_000
        const val TRY_LOGIN_FREQUENCY_MS = 30_000L
        const val INFO_CHANGE_TIME_FREQUENCY_MS = 120_000L
    }
}
