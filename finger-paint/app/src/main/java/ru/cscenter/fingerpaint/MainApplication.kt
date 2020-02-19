package ru.cscenter.fingerpaint

import android.app.Application
import ru.cscenter.fingerpaint.db.DbController
import ru.cscenter.fingerpaint.resources.GameResourceParser
import ru.cscenter.fingerpaint.resources.GameResources
import ru.cscenter.fingerpaint.synchronization.SynchronizeController
import ru.cscenter.fingerpaint.ui.settings.Settings

class MainApplication : Application() {
    companion object {
        var isLoading = true
        lateinit var dbController: DbController
        lateinit var settings: Settings
        lateinit var gameResources: GameResources
        lateinit var synchronizeController: SynchronizeController
    }

    override fun onCreate() {
        super.onCreate()
        dbController = DbController(applicationContext)
        settings = Settings(applicationContext)
        gameResources = GameResourceParser(applicationContext, R.xml.game_config).parseXML()
        synchronizeController = SynchronizeController()
    }

}
