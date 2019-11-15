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

    var currentUser: User? = db.getCurrentUser()

    fun getAllNames() = db.getAllNames()
    fun getUser(id: Int) = db.getUser(id)
    fun setUser(user: User) = db.setUser(user)
    fun deleteUser(user: User) = db.deleteUser(user)
    fun insertUser(name: String): Boolean
            = db.insertUser(User(name = name)) != (-1).toLong()
    fun setCurrentUser(userId: Int) {
        if (currentUser == null) {
            db.addCurrentUser(CurrentUser(userId))
        } else {
            db.setCurrentUser(CurrentUser(userId))
        }
        currentUser = db.getCurrentUser()
    }
}
