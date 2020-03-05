package ru.cscenter.fingerpaint.ui.games.singlegames

import android.content.res.Resources
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.GameType
import ru.cscenter.fingerpaint.resources.Figure
import ru.cscenter.fingerpaint.resources.MyColor
import ru.cscenter.fingerpaint.service.figuresRandom
import ru.cscenter.fingerpaint.service.images.setImage
import ru.cscenter.fingerpaint.ui.games.base.BaseGameActivity
import ru.cscenter.fingerpaint.ui.games.base.ChooseGame

class ChooseFigureGame(config: Config, gameActivity: BaseGameActivity) :
    ChooseGame(config, gameActivity) {

    override val gameType: GameType = GameType.CHOOSE_FIGURE

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
                correctImageViewSetter = setImage(correctFigure.resourceId, resources, color.color),
                incorrectImageViewSetter = setImage(
                    incorrectFigure.resourceId,
                    resources,
                    color.color
                )
            )
        }
    }

}
