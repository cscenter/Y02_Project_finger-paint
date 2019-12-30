package ru.cscenter.fingerpaint.ui.games.base

import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic

abstract class BaseGameCallback(private val gameActivity: BaseGameActivity) {
    abstract fun updateStatistics(statistic: Statistic, result: GameResult): Statistic
    abstract fun nextGame(): Game?
    open fun onResult(result: GameResult) {

        gameActivity.supportFragmentManager.popBackStack()

        setStatistics(result)

        val nextGame = nextGame()
        if (result == GameResult.SUCCESS && nextGame != null) {
            gameActivity.runGame(nextGame)
        } else {
            val message = getResultMessage(result)
            gameActivity.showFragment(ResultFragment(message))
        }
    }

    private fun setStatistics(result: GameResult) {
        val db = MainApplication.dbController
        db.getCurrentUserStatistics()?.let {
            val statistic = updateStatistics(it, result)
            db.setStatistics(statistic)
        }
    }

    private fun getResultMessage(result: GameResult) = when (result) {
        GameResult.SUCCESS -> gameActivity.getString(R.string.success_message)
        GameResult.FAIL -> gameActivity.getString(R.string.fail_message)
    }
}
