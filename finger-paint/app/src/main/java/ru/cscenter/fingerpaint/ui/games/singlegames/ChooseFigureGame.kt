package ru.cscenter.fingerpaint.ui.games.singlegames

import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.ui.games.base.*
import ru.cscenter.fingerpaint.ui.games.images.colorsRandom
import ru.cscenter.fingerpaint.ui.games.images.figuresRandom
import ru.cscenter.fingerpaint.ui.games.images.getImage

class ChooseFigureGame(private val gameActivity: BaseGameActivity) : SingleGame,
    BaseGameCallback(gameActivity) {
    override fun nextGame(): Game? = ChooseFigureColorGame(gameActivity).getGame()

    override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
        statistic.figureChooseTotal++
        statistic.figureChooseSuccess += result.toInt()
        return statistic
    }

    override fun getGame(): Game {
        val (correctFigure, incorrectFigure) = figuresRandom.getRandomPair()
        val color = colorsRandom.getRandomValue()
        val task = gameActivity.resources.getString(R.string.choose_figure_task, correctFigure.name)
        return ChooseGame(
            question = task,
            correctImageSupplier = getImage(
                correctFigure.resourceId,
                gameActivity.resources,
                color.color
            ),
            incorrectImageSupplier = getImage(
                incorrectFigure.resourceId,
                gameActivity.resources,
                color.color
            ),
            callback = this
        )
    }
}
