package ru.cscenter.fingerpaint.ui.games.singlegames

import android.content.res.Resources
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.GameType
import ru.cscenter.fingerpaint.resources.Figure
import ru.cscenter.fingerpaint.resources.MyColor
import ru.cscenter.fingerpaint.service.colorsRandom
import ru.cscenter.fingerpaint.service.figuresRandom
import ru.cscenter.fingerpaint.service.images.setImage
import ru.cscenter.fingerpaint.ui.games.base.BaseGameActivity
import ru.cscenter.fingerpaint.ui.games.base.ChooseGame

class ChooseFigureColorGame(config: Config, gameActivity: BaseGameActivity) :
    ChooseGame(config, gameActivity) {

    override val gameType: GameType = GameType.CHOOSE_FIGURE_COLOR

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
                correctImageViewSetter = setImage(
                    correctFigure.resourceId,
                    resources,
                    correctColor.color
                ),
                incorrectImageViewSetter = setImage(
                    incorrectFigure.resourceId,
                    resources,
                    incorrectColor.color
                )
            )
        }
    }
}
