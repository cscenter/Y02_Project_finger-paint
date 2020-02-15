package ru.cscenter.fingerpaint.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.db.GameType
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.db.User

class StatisticsModel : ViewModel() {
    private val user = MutableLiveData<User>()

    fun getUser(): LiveData<User> = user
    fun setUser(user: User) {
        this.user.value = user
    }

    suspend fun getCurrentUserStatistic(type: GameType) =
        withContext(Dispatchers.IO) { MainApplication.dbController.getCurrentUserStatistics(type) }

    suspend fun setStatistic(statistic: Statistic) =
        withContext(Dispatchers.IO) { MainApplication.dbController.setStatistics(statistic) }

    fun getUserAllStatistics() =
        MainApplication.dbController.getUserAllStatistics(user.value?.id ?: -1)
}
