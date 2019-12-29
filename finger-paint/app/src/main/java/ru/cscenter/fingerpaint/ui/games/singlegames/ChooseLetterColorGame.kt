package ru.cscenter.fingerpaint.ui.games.singlegames

import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.ui.games.base.*
import ru.cscenter.fingerpaint.ui.games.images.colorsRandom
import ru.cscenter.fingerpaint.ui.games.images.getImage
import ru.cscenter.fingerpaint.ui.games.images.lettersRandom

class ChooseLetterColorGame(private val gameActivity: BaseGameActivity) : SingleGame,
    BaseGameCallback(gameActivity) {
    override fun nextGame(): Game? = DrawingLetterGame(gameActivity).getGame()

    override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
        statistic.letterColorChooseTotal++
        statistic.letterColorChooseSuccess += result.toInt()
        return statistic
    }

    override fun getGame(): Game {
        val (letter1, letter2) = lettersRandom.getRandomPair()
        val (correctColor, incorrectColor) = colorsRandom.getRandomPair()
        val task =
            gameActivity.resources.getString(R.string.choose_letter_color_task, correctColor.text)
        return ChooseGame(
            question = task,
            correctImageSupplier = getImage(
                letter1.resourceId,
                gameActivity.resources,
                correctColor.color
            ),
            incorrectImageSupplier = getImage(
                letter2.resourceId,
                gameActivity.resources,
                incorrectColor.color
            ),
            callback = this
        )
    }
}
