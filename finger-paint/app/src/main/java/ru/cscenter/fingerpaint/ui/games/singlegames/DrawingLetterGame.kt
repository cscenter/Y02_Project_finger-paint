package ru.cscenter.fingerpaint.ui.games.singlegames

import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.ui.games.base.*
import ru.cscenter.fingerpaint.ui.games.images.colorsRandom
import ru.cscenter.fingerpaint.ui.games.images.getLetterImage
import ru.cscenter.fingerpaint.ui.games.images.getLetterImageCompressed
import ru.cscenter.fingerpaint.ui.games.images.lettersRandom
import ru.cscenter.fingerpaint.ui.games.tasks.getDrawLetterTask

private val letterThresholds = Pair(0.7f, 0.1f)

class DrawingLetterGame(gameActivity: BaseGameActivity) : SingleGame,
    BaseGameCallback(gameActivity) {
    override fun nextGame(): Game? = null

    override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
        statistic.contouringTotal++
        statistic.contouringSuccess += result.toInt()
        return statistic
    }

    override fun getGame(): Game {
        val letter = lettersRandom.getRandomValue()
        val color = colorsRandom.getRandomValue()
        return DrawingGame(
            question = getDrawLetterTask(),
            imageSupplier = getLetterImageCompressed(letter),
            backgroundImageSupplier = getLetterImage(letter),
            paintColor = color,
            thresholds = letterThresholds,
            callback = this
        )
    }
}
