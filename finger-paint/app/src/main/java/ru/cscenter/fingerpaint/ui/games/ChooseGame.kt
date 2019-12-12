package ru.cscenter.fingerpaint.ui.games

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import ru.cscenter.fingerpaint.R
import kotlin.random.Random


private const val MAX_ATTEMPTS = 3

class ChooseGame(
    private val question: String,
    private val correctImageSupplier: (width: Int, height: Int) -> Bitmap,
    private val incorrectImageSupplier: (width: Int, height: Int) -> Bitmap,
    private val callback: GameActivity.GameCallback
) : Game() {

    private var attempts = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_choose_game, container, false)

        val questionView: TextView = root.findViewById(R.id.question)
        questionView.text = question

        var correctChooseView: ImageView = root.findViewById(R.id.first_choose)
        var incorrectChooseView: ImageView = root.findViewById(R.id.second_choose)
        if (Random.nextBoolean()) {
            correctChooseView = incorrectChooseView.also { incorrectChooseView = correctChooseView }
        }

        setImageAsSoonAsPossible(correctChooseView, correctImageSupplier)
        setImageAsSoonAsPossible(incorrectChooseView, incorrectImageSupplier)

        correctChooseView.setOnClickListener {
            callback.onResult(GameResult.SUCCESS)
        }

        incorrectChooseView.setOnClickListener {
            if (attempts >= MAX_ATTEMPTS) {
                callback.onResult(GameResult.FAIL)
            } else {
                attempts++
                // TODO vibrate here
                Snackbar.make(root, "Wrong. Try again.", Snackbar.LENGTH_LONG).show()
            }
        }
        return root
    }
}