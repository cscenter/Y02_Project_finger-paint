package ru.cscenter.fingerpaint.ui.games

import ru.cscenter.fingerpaint.ui.games.base.BaseGameActivity
import ru.cscenter.fingerpaint.ui.games.singlegames.ChooseLetterGame

class Letters2GameActivity : BaseGameActivity() {
    override fun firstGame() = ChooseLetterGame(this)
}
