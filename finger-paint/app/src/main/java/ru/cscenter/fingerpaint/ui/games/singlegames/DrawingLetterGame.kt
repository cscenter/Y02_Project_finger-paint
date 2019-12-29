package ru.cscenter.fingerpaint.ui.games.singlegames

import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.ui.games.base.*
import ru.cscenter.fingerpaint.ui.games.images.colorsRandom
import ru.cscenter.fingerpaint.ui.games.images.getImage
import ru.cscenter.fingerpaint.ui.games.images.getImageCompressed
import ru.cscenter.fingerpaint.ui.games.images.lettersRandom

private val letterThresholds = Pair(0.7f, 0.1f)

class DrawingLetterGame(private val gameActivity: BaseGameActivity) : SingleGame,
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
        val task = gameActivity.resources.getString(R.string.letter_contouring_task)
        return DrawingGame(
            question = task,
            imageSupplier = getImageCompressed(letter.resourceId, gameActivity.resources),
            backgroundImageSupplier = getImage(letter.resourceId, gameActivity.resources),
            paintColor = color.color,
            thresholds = letterThresholds,
            callback = this
        )
    }
}
