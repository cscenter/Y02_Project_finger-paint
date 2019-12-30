package ru.cscenter.fingerpaint.ui.games

import ru.cscenter.fingerpaint.ui.games.base.BaseGameActivity
import ru.cscenter.fingerpaint.ui.games.singlegames.ChooseLetterColorGame

class Letters1GameActivity : BaseGameActivity() {
    override fun firstGame() = ChooseLetterColorGame(this)
}
