package ru.cscenter.fingerpaint.ui.games

import ru.cscenter.fingerpaint.ui.games.base.BaseGameActivity
import ru.cscenter.fingerpaint.ui.games.singlegames.ChooseFigureGame

class FiguresGameActivity : BaseGameActivity() {
    override fun firstGame() = ChooseFigureGame(this)
}
