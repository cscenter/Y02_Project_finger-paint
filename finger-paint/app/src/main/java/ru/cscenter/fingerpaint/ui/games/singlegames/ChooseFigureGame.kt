package ru.cscenter.fingerpaint.ui.games.singlegames

import android.content.res.Resources
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.resources.Figure
import ru.cscenter.fingerpaint.resources.MyColor
import ru.cscenter.fingerpaint.service.figuresRandom
import ru.cscenter.fingerpaint.service.images.getImage
import ru.cscenter.fingerpaint.ui.games.base.BaseGameActivity
import ru.cscenter.fingerpaint.ui.games.base.ChooseGame
import ru.cscenter.fingerpaint.ui.games.base.GameResult
import ru.cscenter.fingerpaint.ui.games.base.toInt

class ChooseFigureGame(config: Config, gameActivity: BaseGameActivity) :
    ChooseGame(config, gameActivity) {

    override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
        statistic.figureChooseTotal++
        statistic.figureChooseSuccess += result.toInt()
        return statistic
    }

    companion object {
        fun createConfig(
            resources: Resources,
            color: MyColor,
            correctFigure: Figure
        ): Config {
            val incorrectFigure = figuresRandom.getRandomNonEqualValue(correctFigure)
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
