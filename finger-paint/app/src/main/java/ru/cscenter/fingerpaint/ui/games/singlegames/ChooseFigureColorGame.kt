package ru.cscenter.fingerpaint.ui.games.singlegames

import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.ui.games.base.*
import ru.cscenter.fingerpaint.ui.games.images.colorsRandom
import ru.cscenter.fingerpaint.ui.games.images.figuresRandom
import ru.cscenter.fingerpaint.ui.games.images.getFigureImage
import ru.cscenter.fingerpaint.ui.games.tasks.getChooseFigureColorTask

class ChooseFigureColorGame(private val gameActivity: BaseGameActivity) : SingleGame,
    BaseGameCallback(gameActivity) {
    override fun nextGame(): Game? = DrawingFigureGame(gameActivity).getGame()

    override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
        statistic.figureColorChooseTotal++
        statistic.figureColorChooseSuccess += result.toInt()
        return statistic
    }

    override fun getGame(): Game {
        val (figure1, figure2) = figuresRandom.getRandomPair()
        val (correctColor, incorrectColor) = colorsRandom.getRandomPair()
        return ChooseGame(
            question = getChooseFigureColorTask(correctColor),
            correctImageSupplier = getFigureImage(figure1, correctColor, true),
            incorrectImageSupplier = getFigureImage(figure2, incorrectColor, true),
            callback = this
        )
    }
}
