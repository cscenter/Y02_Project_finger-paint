package ru.cscenter.fingerpaint.ui.games.singlegames

import android.content.res.Resources
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.service.colorsRandom
import ru.cscenter.fingerpaint.service.images.getImage
import ru.cscenter.fingerpaint.service.images.getImageCompressed
import ru.cscenter.fingerpaint.service.lettersRandom
import ru.cscenter.fingerpaint.ui.games.base.*

class DrawingLetterGame(gameActivity: BaseGameActivity) :
    DrawingGame(createConfig(gameActivity.resources), gameActivity) {

    override fun nextGame(): Game? = null
    override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
        statistic.contouringTotal++
        statistic.contouringSuccess += result.toInt()
        return statistic
    }

    companion object {
        private val letterThresholds = Pair(0.7f, 0.1f)

        private fun createConfig(resources: Resources): Config {
            val letter = lettersRandom.getRandomValue()
            val color = colorsRandom.getRandomValue()
            val task = resources.getString(R.string.letter_contouring_task)
            return Config(
                question = task,
                imageSupplier = getImageCompressed(letter.resourceId, resources),
                backgroundImageSupplier = getImage(letter.resourceId, resources),
                paintColor = color.color,
                thresholds = letterThresholds
            )
        }
    }
}
