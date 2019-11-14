package ru.cscenter.fingerpaint.ui.games

import androidx.fragment.app.Fragment

abstract class Game(val onFinished: (Boolean) -> Unit) : Fragment() {

    fun onFinish(result: Boolean) {
        fragmentManager!!.popBackStack()
        onFinished(result)
    }
}
