package ru.cscenter.fingerpaint.ui.games

import androidx.fragment.app.Fragment

abstract class Game : Fragment()


enum class GameResult {
    SUCCESS,
    FAIL
}