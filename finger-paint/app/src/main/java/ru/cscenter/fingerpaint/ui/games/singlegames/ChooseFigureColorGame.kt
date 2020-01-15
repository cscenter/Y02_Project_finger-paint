package ru.cscenter.fingerpaint.ui.games.singlegames

import android.content.res.Resources
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.resources.Figure
import ru.cscenter.fingerpaint.resources.MyColor
import ru.cscenter.fingerpaint.service.colorsRandom
import ru.cscenter.fingerpaint.service.figuresRandom
import ru.cscenter.fingerpaint.service.images.getImage
import ru.cscenter.fingerpaint.ui.games.base.BaseGameActivity
import ru.cscenter.fingerpaint.ui.games.base.ChooseGame
import ru.cscenter.fingerpaint.ui.games.base.GameResult
import ru.cscenter.fingerpaint.ui.games.base.toInt

class ChooseFigureColorGame(config: Config, gameActivity: BaseGameActivity) :
    ChooseGame(config, gameActivity) {

    override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
        statistic.figureColorChooseTotal++
        statistic.figureColorChooseSuccess += result.toInt()
        return statistic
    }


    companion object {
        fun createConfig(
            resources: Resources,
            correctColor: MyColor,
            correctFigure: Figure
        ): Config {
            val incorrectFigure = figuresRandom.getRandomNonEqualValue(correctFigure)
            val incorrectColor = colorsRandom.getRandomNonEqualValue(correctColor)
            val task = resources.getString(R.string.choose_figure_color_task, correctColor.text)
            return Config(
                question = task,
                correctImageSupplier = getImage(
                    correctFigure.resourceId,
                    resources,
                    correctColor.color
                ),
                incorrectImageSupplier = getImage(
                    incorrectFigure.resourceId,
                    resources,
                    incorrectColor.color
                )
            )
        }
    }
}
