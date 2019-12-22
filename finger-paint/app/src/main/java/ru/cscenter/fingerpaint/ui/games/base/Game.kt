package ru.cscenter.fingerpaint.ui.games.base

import androidx.fragment.app.Fragment

abstract class Game : Fragment()


enum class GameResult {
    SUCCESS,
    FAIL
}

fun GameResult.toInt() = when (this) {
    GameResult.SUCCESS -> 1
    GameResult.FAIL -> 0
}
