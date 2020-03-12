package ru.cscenter.fingerpaint.ui.games.singlegames

import android.content.res.Resources
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.GameType
import ru.cscenter.fingerpaint.resources.Letter
import ru.cscenter.fingerpaint.resources.MyColor
import ru.cscenter.fingerpaint.service.colorsRandom
import ru.cscenter.fingerpaint.service.images.setImage
import ru.cscenter.fingerpaint.service.lettersRandom
import ru.cscenter.fingerpaint.ui.games.base.BaseGameActivity
import ru.cscenter.fingerpaint.ui.games.base.ChooseGame

class ChooseLetterColorGame(config: Config, gameActivity: BaseGameActivity) :
    ChooseGame(config, gameActivity) {

    override val gameType: GameType = GameType.CHOOSE_LETTER_COLOR

    companion object {
        fun createConfig(
            resources: Resources,
            correctColor: MyColor,
            correctLetter: Letter
        ): Config {
            val incorrectLetter = lettersRandom.getRandomNonEqualValue(correctLetter)
            val incorrectColor = colorsRandom.getRandomNonEqualValue(correctColor)
            val task = resources.getString(R.string.choose_letter_color_task, correctColor.text)
            return Config(
                question = task,
                correctImageViewSetter = setImage(
                    correctLetter.resourceId,
                    resources,
                    correctColor.color
                ),
                incorrectImageViewSetter = setImage(
                    incorrectLetter.resourceId,
                    resources,
                    incorrectColor.color
                )
            )
        }
    }
}
