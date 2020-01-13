package ru.cscenter.fingerpaint.ui.games.singlegames

import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.ui.games.base.*
import ru.cscenter.fingerpaint.ui.games.images.colorsRandom
import ru.cscenter.fingerpaint.ui.games.images.figuresRandom
import ru.cscenter.fingerpaint.ui.games.images.getImage

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
        val task =
            gameActivity.resources.getString(R.string.choose_figure_color_task, correctColor.text)
        return ChooseGame(
            question = task,
            correctImageSupplier = getImage(
                figure1.resourceId,
                gameActivity.resources,
                correctColor.color
            ),
            incorrectImageSupplier = getImage(
                figure2.resourceId,
                gameActivity.resources,
                incorrectColor.color
            ),
            callback = this
        )
    }
}
