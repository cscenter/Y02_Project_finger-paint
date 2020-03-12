package ru.cscenter.fingerpaint.ui.games.base

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.cscenter.fingerpaint.R

abstract class BaseGameActivity : AppCompatActivity() {
    private lateinit var iterator: Iterator<Game>

    abstract fun configureGames(): List<Game>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        lockOrientation()
        startGame()
    }

    fun startGame() {
        iterator = configureGames().iterator()
        runGame(iterator.next())
    }

    fun onGameFinished(result: GameResult) {
        if (result == GameResult.SUCCESS && iterator.hasNext()) {
            runGame(iterator.next())
        } else {
            val message = getResultMessage(result)
            showFragment(ResultFragment(message))
        }
    }

    private fun lockOrientation() {
        val currentOrientation = resources.configuration.orientation
        requestedOrientation = if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }
    }

    private fun showFragment(fragment: Fragment) = supportFragmentManager
        .beginTransaction()
        .replace(R.id.fragment_container, fragment)
        .commit()

    private fun runGame(game: Game) = showFragment(game)

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun getResultMessage(result: GameResult) = when (result) {
        GameResult.SUCCESS -> getString(R.string.success_message)
        GameResult.FAIL -> getString(R.string.fail_message)
    }

}
