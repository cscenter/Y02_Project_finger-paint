package ru.cscenter.fingerpaint.ui.games

import ru.cscenter.fingerpaint.service.colorsRandom
import ru.cscenter.fingerpaint.service.lettersRandom
import ru.cscenter.fingerpaint.ui.games.base.BaseGameActivity
import ru.cscenter.fingerpaint.ui.games.base.Game
import ru.cscenter.fingerpaint.ui.games.singlegames.ChooseLetterColorGame
import ru.cscenter.fingerpaint.ui.games.singlegames.ChooseLetterGame
import ru.cscenter.fingerpaint.ui.games.singlegames.DrawingLetterGame

class Letters2GameActivity : BaseGameActivity() {
    override fun configureGames(): List<Game> {
        val color = colorsRandom.getRandomValue()
        val letter = lettersRandom.getRandomValue()

        val chooseLetterGameConfig = ChooseLetterGame.createConfig(resources, color, letter)
        val chooseLetterColorGameConfig =
            ChooseLetterColorGame.createConfig(resources, color, letter)
        val drawLetterGameConfig = DrawingLetterGame.createConfig(resources, color, letter)

        return listOf(
            ChooseLetterGame(chooseLetterGameConfig, this),
            ChooseLetterColorGame(chooseLetterColorGameConfig, this),
            DrawingLetterGame(drawLetterGameConfig, this)
        )
    }
}
