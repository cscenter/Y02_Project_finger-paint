package ru.cscenter.fingerpaint.ui.games.singlegames

import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.ui.games.base.*
import ru.cscenter.fingerpaint.ui.games.images.colorsRandom
import ru.cscenter.fingerpaint.ui.games.images.figuresRandom
import ru.cscenter.fingerpaint.ui.games.images.getFigureImage
import ru.cscenter.fingerpaint.ui.games.images.getFigureImageCompressed
import ru.cscenter.fingerpaint.ui.games.tasks.getDrawFigureTask

private val figureThresholds = Pair(0.8f, 0.1f)

class DrawingFigureGame(gameActivity: BaseGameActivity) : SingleGame,
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
        return DrawingGame(
            question = getDrawFigureTask(figure),
            imageSupplier = getFigureImageCompressed(figure),
            backgroundImageSupplier = getFigureImage(figure, isFilled = false),
            paintColor = color,
            thresholds = figureThresholds,
            callback = this
        )
    }
}
