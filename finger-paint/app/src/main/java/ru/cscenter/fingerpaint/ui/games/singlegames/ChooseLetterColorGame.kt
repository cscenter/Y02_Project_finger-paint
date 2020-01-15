package ru.cscenter.fingerpaint.ui.games.singlegames

import android.content.res.Resources
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.resources.Letter
import ru.cscenter.fingerpaint.resources.MyColor
import ru.cscenter.fingerpaint.service.colorsRandom
import ru.cscenter.fingerpaint.service.images.getImage
import ru.cscenter.fingerpaint.service.lettersRandom
import ru.cscenter.fingerpaint.ui.games.base.BaseGameActivity
import ru.cscenter.fingerpaint.ui.games.base.ChooseGame
import ru.cscenter.fingerpaint.ui.games.base.GameResult
import ru.cscenter.fingerpaint.ui.games.base.toInt

class ChooseLetterColorGame(config: Config, gameActivity: BaseGameActivity) :
    ChooseGame(config, gameActivity) {

    override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
        statistic.letterColorChooseTotal++
        statistic.letterColorChooseSuccess += result.toInt()
        return statistic
    }

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
                correctImageSupplier = getImage(
                    correctLetter.resourceId,
                    resources,
                    correctColor.color
                ),
                incorrectImageSupplier = getImage(
                    incorrectLetter.resourceId,
                    resources,
                    incorrectColor.color
                )
            )
        }
    }
}
