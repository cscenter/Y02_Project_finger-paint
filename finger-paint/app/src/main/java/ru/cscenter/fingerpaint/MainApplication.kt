package ru.cscenter.fingerpaint

import androidx.multidex.MultiDexApplication
import ru.cscenter.fingerpaint.db.DbController
import ru.cscenter.fingerpaint.resources.GameResourceParser
import ru.cscenter.fingerpaint.resources.GameResources
import ru.cscenter.fingerpaint.synchronization.SynchronizeController
import ru.cscenter.fingerpaint.ui.settings.Settings

class MainApplication : MultiDexApplication() {
    companion object {
        lateinit var dbController: DbController
        lateinit var settings: Settings
        lateinit var gameResources: GameResources
        lateinit var synchronizeController: SynchronizeController
        lateinit var baseUrl: String
    }

    override fun onCreate() {
        super.onCreate()
        dbController = DbController(applicationContext)
        settings = Settings(applicationContext)
        gameResources = GameResourceParser(applicationContext, R.xml.game_config).parseXML()
        baseUrl = getString(R.string.base_url)
    }

}
