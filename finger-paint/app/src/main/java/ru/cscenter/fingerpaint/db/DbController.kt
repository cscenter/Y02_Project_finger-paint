package ru.cscenter.fingerpaint.db

import android.content.Context
import androidx.room.Room

private const val DATABASE_NAME: String = "users.db"

class DbController(context: Context) {
    private val database = Room
        .databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
        .build()
    private val db = database.dao()

    val currentUser = db.getCurrentUser()
    val users = db.getUsers()

    suspend fun setCurrentUser(userId: Int) = db.setCurrentUser(CurrentUser(userId))
    suspend fun hasCurrentUser() = db.hasCurrentUser() != null
    suspend fun getCurrentUser() = db.selectCurrentUser()

    suspend fun getUser(id: Int) = db.getUser(id)
    suspend fun setUser(user: User): Boolean = db.setUser(user) > 0
    suspend fun setUsers(users: List<User>) = db.setUser(*users.toTypedArray())
    suspend fun insertUser(name: String, status: UserStatus = UserStatus.SYNCHRONIZED) =
        db.insertUser(name, status)

    suspend fun selectUsers() = db.selectUsers()
    suspend fun insertUsers(users: List<User>) = db.insertUsers(users)
    suspend fun deleteUser(user: User) = db.deleteUser(user)
    suspend fun syncUsers(users: List<User>) = db.syncUsers(users)

    suspend fun setStatistics(vararg statistic: Statistic) = db.insertStatistics(*statistic)
    fun getUserAllStatistics(id: Int) = db.getUserAllStatistics(id)
    suspend fun selectUserAllStatistics(id: Int) = db.selectUserAllStatistics(id)

    suspend fun getCurrentUserStatistics(type: GameType): Statistic? = currentUser.value?.let {
        db.getCurrentUserStatistics(type)
            ?: Statistic(userId = it.id, type = type, date = currentDay())
    }

    fun clear() = database.clearAllTables()

    suspend fun selectCachedResults() = db.selectCachedResults()
    suspend fun addGameResult(result: CachedGameResult) {
        if (getUser(result.userId)!!.status != UserStatus.NEW) {
            db.addGameResult(result)
        }
    }

    suspend fun clearCache() = db.removeCachedResults()
}
