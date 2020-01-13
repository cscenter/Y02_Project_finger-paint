package ru.cscenter.fingerpaint.ui.games.singlegames

import android.content.res.Resources
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.service.colorsRandom
import ru.cscenter.fingerpaint.service.figuresRandom
import ru.cscenter.fingerpaint.service.images.getImage
import ru.cscenter.fingerpaint.ui.games.base.*

class ChooseFigureGame(private val gameActivity: BaseGameActivity) :
    ChooseGame(createConfig(gameActivity.resources), gameActivity) {

    override fun nextGame(): Game? = ChooseFigureColorGame(gameActivity)
    override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
        statistic.figureChooseTotal++
        statistic.figureChooseSuccess += result.toInt()
        return statistic
    }

    companion object {
        private fun createConfig(resources: Resources): Config {
            val (correctFigure, incorrectFigure) = figuresRandom.getRandomPair()
            val color = colorsRandom.getRandomValue()
            val task = resources.getString(R.string.choose_figure_task, correctFigure.name)
            return Config(
                question = task,
                correctImageSupplier = getImage(correctFigure.resourceId, resources, color.color),
                incorrectImageSupplier = getImage(
                    incorrectFigure.resourceId,
                    resources,
                    color.color
                )
            )
        }
    }

}
