package ru.cscenter.fingerpaint.ui.games.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.service.AudioController
import ru.cscenter.fingerpaint.service.MyVibrator
import kotlin.random.Random


private const val MAX_ATTEMPTS = 3

abstract class ChooseGame(private val config: Config, gameActivity: BaseGameActivity) :
    Game(gameActivity) {

    data class Config(
        val question: String,
        val correctImageViewSetter: (ImageView) -> Unit,
        val incorrectImageViewSetter: (ImageView) -> Unit
    )

    private var attempts = 1

    private lateinit var audioController: AudioController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_choose_game, container, false)

        audioController = AudioController(context!!)

        val questionView: TextView = root.findViewById(R.id.question)
        questionView.text = config.question

        val audioButton: ImageView = root.findViewById(R.id.audio_button)
        audioButton.apply {
            if (!audioController.isAvailable) {
                visibility = View.GONE
            }
            setOnClickListener {
                audioController.speak(config.question)
            }
        }

        var correctChooseView: ImageView = root.findViewById(R.id.first_choose)
        var incorrectChooseView: ImageView = root.findViewById(R.id.second_choose)
        if (Random.nextBoolean()) {
            correctChooseView = incorrectChooseView.also { incorrectChooseView = correctChooseView }
        }

        config.correctImageViewSetter(correctChooseView)
        config.incorrectImageViewSetter(incorrectChooseView)

        correctChooseView.setOnClickListener {
            onResult(GameResult.SUCCESS)
        }

        incorrectChooseView.setOnClickListener {
            if (MainApplication.settings.getVibrate()) {
                MyVibrator.vibrate(context!!, MyVibrator.LENGTH_LONG)
            }
            if (attempts >= MAX_ATTEMPTS) {
                onResult(GameResult.FAIL)
            } else {
                attempts++
                Snackbar.make(root, getString(R.string.fail_message), Snackbar.LENGTH_SHORT).show()
            }
        }
        return root
    }

    override fun onPause() {
        super.onPause()
        audioController.shutdown()
    }
}
