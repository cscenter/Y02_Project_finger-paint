package ru.cscenter.fingerpaint.ui.games.singlegames

import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.ui.games.base.*
import ru.cscenter.fingerpaint.ui.games.images.getLetterImage
import ru.cscenter.fingerpaint.ui.games.images.lettersRandom
import ru.cscenter.fingerpaint.ui.games.tasks.getChooseLetterTask

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
        return ChooseGame(
            question = getChooseLetterTask(correctLetter),
            correctImageSupplier = getLetterImage(correctLetter),
            incorrectImageSupplier = getLetterImage(incorrectLetter),
            callback = this
        )
    }
}
