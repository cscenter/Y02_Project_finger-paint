package ru.cscenter.fingerpaint.ui.games.base

import androidx.fragment.app.Fragment
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.db.Statistic

abstract class Game(private val gameActivity: BaseGameActivity) : Fragment() {
    abstract fun updateStatistics(statistic: Statistic, result: GameResult): Statistic

    open fun onResult(result: GameResult) {
        gameActivity.supportFragmentManager.popBackStack()
        setStatistics(result)
        gameActivity.onGameFinished(result)
    }

    private fun setStatistics(result: GameResult) {
        val db = MainApplication.dbController
        db.getCurrentUserStatistics()?.let {
            val statistic = updateStatistics(it, result)
            db.setStatistics(statistic)
        }
    }
}


enum class GameResult {
    SUCCESS,
    FAIL
}

fun GameResult.toInt() = when (this) {
    GameResult.SUCCESS -> 1
    GameResult.FAIL -> 0
}
