package ru.cscenter.fingerpaint.ui.games.singlegames

import android.content.res.Resources
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.service.colorsRandom
import ru.cscenter.fingerpaint.service.figuresRandom
import ru.cscenter.fingerpaint.service.images.getImage
import ru.cscenter.fingerpaint.ui.games.base.*

class ChooseFigureColorGame(private val gameActivity: BaseGameActivity) :
    ChooseGame(createConfig(gameActivity.resources), gameActivity) {

    override fun nextGame(): Game? = DrawingFigureGame(gameActivity)
    override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
        statistic.figureColorChooseTotal++
        statistic.figureColorChooseSuccess += result.toInt()
        return statistic
    }


    companion object {
        private fun createConfig(resources: Resources): Config {
            val (figure1, figure2) = figuresRandom.getRandomPair()
            val (correctColor, incorrectColor) = colorsRandom.getRandomPair()
            val task = resources.getString(R.string.choose_figure_color_task, correctColor.text)
            return Config(
                question = task,
                correctImageSupplier = getImage(
                    figure1.resourceId,
                    resources,
                    correctColor.color
                ),
                incorrectImageSupplier = getImage(
                    figure2.resourceId,
                    resources,
                    incorrectColor.color
                )
            )
        }
    }
}
