package ru.cscenter.fingerpaint.ui.games.singlegames

import android.content.res.Resources
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.GameType
import ru.cscenter.fingerpaint.resources.Letter
import ru.cscenter.fingerpaint.resources.MyColor
import ru.cscenter.fingerpaint.service.images.getImage
import ru.cscenter.fingerpaint.service.images.getImageCompressed
import ru.cscenter.fingerpaint.ui.games.base.BaseGameActivity
import ru.cscenter.fingerpaint.ui.games.base.DrawingGame

class DrawingLetterGame(config: Config, gameActivity: BaseGameActivity) :
    DrawingGame(config, gameActivity) {

    override val gameType: GameType = GameType.DRAW_LETTER

    companion object {
        private val letterThresholds = Pair(0.7f, 0.1f)

        fun createConfig(resources: Resources, color: MyColor, letter: Letter): Config {
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
