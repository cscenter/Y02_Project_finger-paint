package ru.cscenter.fingerpaint.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.cscenter.fingerpaint.R

class ResultFragment(private val message: String) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_result, container, false)

        val messageView: TextView = root.findViewById(R.id.result_message)
        messageView.text = message

        val restartButton: Button = root.findViewById(R.id.repeat_button)
        restartButton.setOnClickListener {
            (activity as GameActivity).startGame()
        }

        val exitButton: Button = root.findViewById(R.id.exit_button)
        exitButton.setOnClickListener {
            activity!!.finish()
        }

        return root
    }
}
