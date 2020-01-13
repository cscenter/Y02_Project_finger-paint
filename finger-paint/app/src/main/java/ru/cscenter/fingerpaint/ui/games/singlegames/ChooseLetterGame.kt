package ru.cscenter.fingerpaint.ui.games.singlegames

import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.ui.games.base.*
import ru.cscenter.fingerpaint.ui.games.images.colorsRandom
import ru.cscenter.fingerpaint.ui.games.images.getImage
import ru.cscenter.fingerpaint.ui.games.images.lettersRandom

class ChooseLetterGame(private val gameActivity: BaseGameActivity) : SingleGame,
    BaseGameCallback(gameActivity) {
    override fun nextGame(): Game? = ChooseLetterColorGame(gameActivity).getGame()

    override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
        statistic.letterChooseTotal++
        statistic.letterChooseSuccess += result.toInt()
        return statistic
    }

    override fun getGame(): Game {
        val (correctLetter, incorrectLetter) = lettersRandom.getRandomPair()
        val color = colorsRandom.getRandomValue()
        val task = gameActivity.resources.getString(R.string.choose_letter_task, correctLetter.name)
        return ChooseGame(
            question = task,
            correctImageSupplier = getImage(
                correctLetter.resourceId,
                gameActivity.resources,
                color.color
            ),
            incorrectImageSupplier = getImage(
                incorrectLetter.resourceId,
                gameActivity.resources,
                color.color
            ),
            callback = this
        )
    }
}
