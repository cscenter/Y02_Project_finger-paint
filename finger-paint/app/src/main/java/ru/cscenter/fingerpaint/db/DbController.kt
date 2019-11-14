package ru.cscenter.fingerpaint.db

import android.content.Context
import androidx.room.Room
import ru.cscenter.fingerpaint.R

class DbController(context: Context) {
    private val db = Room
        .databaseBuilder(context, AppDatabase::class.java, context.resources.getString(R.string.database_name))
        .allowMainThreadQueries()
        .build()
        .dao()

    var currentUser: User? = db.getCurrentUser()

    fun getAllNames() = db.getAllNames()
    fun getUser(id: Int) = db.getUser(id)
    fun setUser(user: User) = db.setUser(user)
    fun deleteUser(user: User) = db.deleteUser(user)
    fun insertUser(firstName: String, lastName: String)
            = db.insertUser(User(firstName = firstName, lastName = lastName))
    fun setCurrentUser(userId: Int) {
        if (currentUser == null) {
            db.addCurrentUser(CurrentUser(userId))
        } else {
            db.setCurrentUser(CurrentUser(userId))
        }
        currentUser = db.getCurrentUser()
    }
}
