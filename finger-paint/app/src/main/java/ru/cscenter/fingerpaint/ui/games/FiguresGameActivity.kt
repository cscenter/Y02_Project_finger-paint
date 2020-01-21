package ru.cscenter.fingerpaint.ui.games

import ru.cscenter.fingerpaint.service.colorsRandom
import ru.cscenter.fingerpaint.service.figuresRandom
import ru.cscenter.fingerpaint.ui.games.base.BaseGameActivity
import ru.cscenter.fingerpaint.ui.games.base.Game
import ru.cscenter.fingerpaint.ui.games.singlegames.ChooseFigureColorGame
import ru.cscenter.fingerpaint.ui.games.singlegames.ChooseFigureGame
import ru.cscenter.fingerpaint.ui.games.singlegames.DrawingFigureGame

class FiguresGameActivity : BaseGameActivity() {
    override fun configureGames(): List<Game> {
        val color = colorsRandom.getRandomValue()
        val figure = figuresRandom.getRandomValue()

        val chooseFigureGameConfig = ChooseFigureGame.createConfig(resources, color, figure)
        val chooseFigureColorGameConfig =
            ChooseFigureColorGame.createConfig(resources, color, figure)
        val drawFigureGameConfig = DrawingFigureGame.createConfig(resources, color, figure)

        return listOf(
            ChooseFigureGame(chooseFigureGameConfig, this),
            ChooseFigureColorGame(chooseFigureColorGameConfig, this),
            DrawingFigureGame(drawFigureGameConfig, this)
        )
    }
}
