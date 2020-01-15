package ru.cscenter.fingerpaint.ui.games.singlegames

import android.content.res.Resources
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.resources.Figure
import ru.cscenter.fingerpaint.resources.MyColor
import ru.cscenter.fingerpaint.service.images.getImage
import ru.cscenter.fingerpaint.service.images.getImageCompressed
import ru.cscenter.fingerpaint.ui.games.base.BaseGameActivity
import ru.cscenter.fingerpaint.ui.games.base.DrawingGame
import ru.cscenter.fingerpaint.ui.games.base.GameResult
import ru.cscenter.fingerpaint.ui.games.base.toInt

class DrawingFigureGame(config: Config, gameActivity: BaseGameActivity) :
    DrawingGame(config, gameActivity) {

    override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
        statistic.drawingTotal++
        statistic.drawingSuccess += result.toInt()
        return statistic
    }

    companion object {
        private val figureThresholds = Pair(0.8f, 0.1f)

        fun createConfig(resources: Resources, color: MyColor, figure: Figure): Config {
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
