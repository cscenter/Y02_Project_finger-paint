package ru.cscenter.fingerpaint.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import ru.cscenter.fingerpaint.R

class SimpleGame(onFinished: (Boolean) -> Unit): Game(onFinished) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_simple_game, container, false)

        val yesButton: Button = root.findViewById(R.id.yes_button)
        yesButton.setOnClickListener {
            onFinish(true)
        }

        val noButton: Button = root.findViewById(R.id.no_button)
        noButton.setOnClickListener {
            onFinish(false)
        }

        return root
    }
}
