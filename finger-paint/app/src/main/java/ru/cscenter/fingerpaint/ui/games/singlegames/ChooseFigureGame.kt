package ru.cscenter.fingerpaint.ui.games.singlegames

import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.ui.games.base.*
import ru.cscenter.fingerpaint.ui.games.images.colorsRandom
import ru.cscenter.fingerpaint.ui.games.images.figuresRandom
import ru.cscenter.fingerpaint.ui.games.images.getFigureImage
import ru.cscenter.fingerpaint.ui.games.tasks.getChooseFigureTask

class ChooseFigureGame(private val gameActivity: BaseGameActivity) : SingleGame,
    BaseGameCallback(gameActivity) {
    override fun nextGame(): Game? = ChooseFigureColorGame(gameActivity).getGame()

    override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
        statistic.figureChooseTotal++
        statistic.figureChooseSuccess += result.toInt()
        return statistic
    }

    override fun getGame(): Game {
        val (correctColor, incorrectColor) = figuresRandom.getRandomPair()
        val color = colorsRandom.getRandomValue()
        return ChooseGame(
            question = getChooseFigureTask(correctColor),
            correctImageSupplier = getFigureImage(correctColor, color, false),
            incorrectImageSupplier = getFigureImage(incorrectColor, color, false),
            callback = this
        )
    }
}
