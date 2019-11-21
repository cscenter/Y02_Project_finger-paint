package ru.cscenter.fingerpaint

import android.app.Application
import ru.cscenter.fingerpaint.db.DbController

class MainApplication : Application() {
    companion object {
        var isLoading = true
        lateinit var dbController: DbController
    }

    override fun onCreate() {
        super.onCreate()
        dbController = DbController(applicationContext)
    }

}
