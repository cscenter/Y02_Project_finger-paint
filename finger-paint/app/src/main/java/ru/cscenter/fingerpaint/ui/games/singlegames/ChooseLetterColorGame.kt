package ru.cscenter.fingerpaint.ui.games.singlegames

import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.ui.games.base.*
import ru.cscenter.fingerpaint.ui.games.images.colorsRandom
import ru.cscenter.fingerpaint.ui.games.images.getLetterImage
import ru.cscenter.fingerpaint.ui.games.images.lettersRandom
import ru.cscenter.fingerpaint.ui.games.tasks.getChooseLetterColorTask

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
        return ChooseGame(
            question = getChooseLetterColorTask(correctColor),
            correctImageSupplier = getLetterImage(letter1, correctColor),
            incorrectImageSupplier = getLetterImage(letter2, incorrectColor),
            callback = this
        )
    }
}
