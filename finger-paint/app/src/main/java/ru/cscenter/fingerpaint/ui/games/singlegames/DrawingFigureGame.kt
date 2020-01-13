package ru.cscenter.fingerpaint.ui.games.singlegames

import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.ui.games.base.*
import ru.cscenter.fingerpaint.ui.games.images.colorsRandom
import ru.cscenter.fingerpaint.ui.games.images.figuresRandom
import ru.cscenter.fingerpaint.ui.games.images.getImage
import ru.cscenter.fingerpaint.ui.games.images.getImageCompressed

private val figureThresholds = Pair(0.8f, 0.1f)

class DrawingFigureGame(private val gameActivity: BaseGameActivity) : SingleGame,
    BaseGameCallback(gameActivity) {
    override fun nextGame(): Game? = null

    override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
        statistic.drawingTotal++
        statistic.drawingSuccess += result.toInt()
        return statistic
    }

    override fun getGame(): Game {
        val figure = figuresRandom.getRandomValue()
        val color = colorsRandom.getRandomValue()
        val task = gameActivity.resources.getString(R.string.draw_figure_tack, figure.name)
        return DrawingGame(
            question = task,
            imageSupplier = getImageCompressed(figure.resourceId, gameActivity.resources),
            backgroundImageSupplier = getImage(figure.resourceId, gameActivity.resources),
            paintColor = color.color,
            thresholds = figureThresholds,
            callback = this
        )
    }
}
