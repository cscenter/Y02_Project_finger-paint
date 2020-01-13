package ru.cscenter.fingerpaint.db

import android.content.Context
import androidx.room.Room

private const val DATABASE_NAME: String = "users.db"

class DbController(context: Context) {
    private val db = Room
        .databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
        .allowMainThreadQueries()
        .build()
        .dao()

    fun currentUser(): User? = db.getCurrentUser()
    fun hasCurrentUser() = currentUser() != null

    fun getAllNames() = db.getAllNames()
    fun getUser(id: Int) = db.getUser(id)
    fun getUserId(name: String) = db.getUserId(name)
    fun setUser(user: User): Boolean = db.setUser(user) > 0
    fun deleteUser(user: User) = db.deleteUser(user)

    private fun getUserStatistics(id: Int): Statistic {
        return db.getUserStatistics(id) ?: Statistic(userId = id, date = currentDay())
    }

    fun getUserAllStatistics(id: Int) = db.getUserAllStatistics(id)
    fun getCurrentUserStatistics(): Statistic? = currentUser()?.let {
        getUserStatistics(it.id)
    }

    fun setStatistics(statistic: Statistic) = db.insertStatistics(statistic)
    fun insertUser(name: String): Boolean = db.insertUser(User(name = name)) != -1L
    fun setCurrentUser(userId: Int) {
        if (currentUser() == null) {
            db.addCurrentUser(CurrentUser(userId))
        } else {
            db.setCurrentUser(CurrentUser(userId))
        }
    }
}
