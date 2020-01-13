package ru.cscenter.fingerpaint.ui.games.singlegames

import android.content.res.Resources
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.service.colorsRandom
import ru.cscenter.fingerpaint.service.images.getImage
import ru.cscenter.fingerpaint.service.lettersRandom
import ru.cscenter.fingerpaint.ui.games.base.*

class ChooseLetterColorGame(private val gameActivity: BaseGameActivity) :
    ChooseGame(createConfig(gameActivity.resources), gameActivity) {

    override fun nextGame(): Game? = DrawingLetterGame(gameActivity)
    override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
        statistic.letterColorChooseTotal++
        statistic.letterColorChooseSuccess += result.toInt()
        return statistic
    }

    companion object {
        private fun createConfig(resources: Resources): Config {
            val (letter1, letter2) = lettersRandom.getRandomPair()
            val (correctColor, incorrectColor) = colorsRandom.getRandomPair()
            val task = resources.getString(R.string.choose_letter_color_task, correctColor.text)
            return Config(
                question = task,
                correctImageSupplier = getImage(
                    letter1.resourceId,
                    resources,
                    correctColor.color
                ),
                incorrectImageSupplier = getImage(
                    letter2.resourceId,
                    resources,
                    incorrectColor.color
                )
            )
        }
    }
}
