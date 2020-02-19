package ru.cscenter.fingerpaint.ui.games.base

import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.db.GameType
import ru.cscenter.fingerpaint.models.StatisticsModel

abstract class Game(private val gameActivity: BaseGameActivity) : Fragment() {
    abstract val gameType: GameType

    open fun onResult(result: GameResult) {
        gameActivity.supportFragmentManager.popBackStack()
        setStatistics(result)
        gameActivity.onGameFinished(result)
    }

    private fun setStatistics(result: GameResult) {
        val statisticsModel: StatisticsModel by gameActivity.viewModels()
        GlobalScope.launch(Dispatchers.Main) {
            statisticsModel.getCurrentUserStatistic(gameType)?.let { statistic ->
                MainApplication.synchronizeController.updateStatistic(statistic, result)
            }
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
