package ru.cscenter.fingerpaint.ui.games.singlegames

import android.content.res.Resources
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.GameType
import ru.cscenter.fingerpaint.resources.Letter
import ru.cscenter.fingerpaint.resources.MyColor
import ru.cscenter.fingerpaint.service.images.getImage
import ru.cscenter.fingerpaint.service.lettersRandom
import ru.cscenter.fingerpaint.ui.games.base.BaseGameActivity
import ru.cscenter.fingerpaint.ui.games.base.ChooseGame

class ChooseLetterGame(config: Config, gameActivity: BaseGameActivity) :
    ChooseGame(config, gameActivity) {

    override val gameType: GameType = GameType.CHOOSE_LETTER

    companion object {
        fun createConfig(
            resources: Resources,
            color: MyColor,
            correctLetter: Letter
        ): Config {
            val incorrectLetter = lettersRandom.getRandomNonEqualValue(correctLetter)
            val task = resources.getString(R.string.choose_letter_task, correctLetter.name)
            return Config(
                question = task,
                correctImageSupplier = getImage(
                    correctLetter.resourceId,
                    resources,
                    color.color
                ),
                incorrectImageSupplier = getImage(
                    incorrectLetter.resourceId,
                    resources,
                    color.color
                )
            )
        }
    }
}
