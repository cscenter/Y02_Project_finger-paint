package ru.cscenter.fingerpaint.ui.games.singlegames

import android.content.res.Resources
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.service.colorsRandom
import ru.cscenter.fingerpaint.service.images.getImage
import ru.cscenter.fingerpaint.service.lettersRandom
import ru.cscenter.fingerpaint.ui.games.base.*

class ChooseLetterGame(private val gameActivity: BaseGameActivity) :
    ChooseGame(createConfig(gameActivity.resources), gameActivity) {

    override fun nextGame(): Game? = ChooseLetterColorGame(gameActivity)
    override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
        statistic.letterChooseTotal++
        statistic.letterChooseSuccess += result.toInt()
        return statistic
    }

    companion object {
        private fun createConfig(resources: Resources): Config {
            val (correctLetter, incorrectLetter) = lettersRandom.getRandomPair()
            val color = colorsRandom.getRandomValue()
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
