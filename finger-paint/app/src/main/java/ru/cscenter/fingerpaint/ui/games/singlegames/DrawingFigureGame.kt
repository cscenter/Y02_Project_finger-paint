package ru.cscenter.fingerpaint.ui.games.singlegames

import android.content.res.Resources
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.service.colorsRandom
import ru.cscenter.fingerpaint.service.figuresRandom
import ru.cscenter.fingerpaint.service.images.getImage
import ru.cscenter.fingerpaint.service.images.getImageCompressed
import ru.cscenter.fingerpaint.ui.games.base.*

class DrawingFigureGame(gameActivity: BaseGameActivity) :
    DrawingGame(createConfig(gameActivity.resources), gameActivity) {

    override fun nextGame(): Game? = null
    override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
        statistic.drawingTotal++
        statistic.drawingSuccess += result.toInt()
        return statistic
    }

    companion object {
        private val figureThresholds = Pair(0.8f, 0.1f)

        private fun createConfig(resources: Resources): Config {
            val figure = figuresRandom.getRandomValue()
            val color = colorsRandom.getRandomValue()
            val task = resources.getString(R.string.draw_figure_tack, figure.name)
            return Config(
                question = task,
                imageSupplier = getImageCompressed(figure.resourceId, resources),
                backgroundImageSupplier = getImage(figure.resourceId, resources),
                paintColor = color.color,
                thresholds = figureThresholds
            )
        }
    }
}
