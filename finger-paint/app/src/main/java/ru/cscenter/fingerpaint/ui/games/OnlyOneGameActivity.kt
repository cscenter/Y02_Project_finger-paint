package ru.cscenter.fingerpaint.ui.games

import ru.cscenter.fingerpaint.service.colorsRandom
import ru.cscenter.fingerpaint.service.figuresRandom
import ru.cscenter.fingerpaint.service.lettersRandom
import ru.cscenter.fingerpaint.ui.games.base.BaseGameActivity
import ru.cscenter.fingerpaint.ui.games.base.Game
import ru.cscenter.fingerpaint.ui.games.singlegames.*

const val ARG_ONLY_ONE_GAME_TYPE = "only-one-game-type"

enum class OnlyOneGameType {
    CHOOSE_FIGURE_GAME,
    CHOOSE_COLOR_GAME,
    CHOOSE_LETTER_GAME,
    DRAW_FIGURE_GAME,
    DRAW_LETTER_GAME,
}

class OnlyOneGameActivity : BaseGameActivity() {
    override fun configureGames(): List<Game> {
        val color = colorsRandom.getRandomValue()
        val figure = figuresRandom.getRandomValue()
        val letter = lettersRandom.getRandomValue()

        when (intent?.extras?.getSerializable(ARG_ONLY_ONE_GAME_TYPE)) {
            OnlyOneGameType.CHOOSE_FIGURE_GAME ->
                return listOf(
                    ChooseFigureGame(
                        ChooseFigureGame.createConfig(resources, color, figure),
                        this
                    )
                )
            OnlyOneGameType.CHOOSE_COLOR_GAME ->
                return listOf(
                    ChooseFigureColorGame(
                        ChooseFigureColorGame.createConfig(resources, color, figure),
                        this
                    )
                )
            OnlyOneGameType.CHOOSE_LETTER_GAME ->
                return listOf(
                    ChooseLetterColorGame(
                        ChooseLetterColorGame.createConfig(resources, color, letter),
                        this
                    )
                )
            OnlyOneGameType.DRAW_FIGURE_GAME ->
                return listOf(
                    DrawingFigureGame(
                        DrawingFigureGame.createConfig(resources, color, figure),
                        this
                    )
                )
            OnlyOneGameType.DRAW_LETTER_GAME ->
                return listOf(
                    DrawingLetterGame(
                        DrawingLetterGame.createConfig(resources, color, letter),
                        this
                    )
                )
            else ->
                throw RuntimeException("Wrong game type")
        }
    }
}
