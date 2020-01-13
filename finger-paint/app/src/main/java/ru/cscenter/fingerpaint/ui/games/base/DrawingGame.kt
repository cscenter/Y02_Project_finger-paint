package ru.cscenter.fingerpaint.ui.games.base

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import ru.cscenter.fingerpaint.R

abstract class DrawingGame(private val config: Config, gameActivity: BaseGameActivity) :
    Game(gameActivity) {

    data class Config(
        val question: String,
        val imageSupplier: (width: Int, height: Int) -> Bitmap,
        val backgroundImageSupplier: (width: Int, height: Int) -> Bitmap,
        val paintColor: Int,
        val thresholds: Pair<Float, Float>
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_drawing_game, container, false)

        val questionView: TextView = root.findViewById(R.id.question)

        questionView.text = config.question

        val goodProgress: ProgressBar = root.findViewById(R.id.good_progress)
        val badProgress: ProgressBar = root.findViewById(R.id.bad_progress)

        val currentLayout: LinearLayout = root.findViewById(R.id.drawing_layout)

        val drawingView = DrawingView(
            context,
            config.imageSupplier,
            config.backgroundImageSupplier,
            config.thresholds,
            config.paintColor,
            Pair(goodProgress, badProgress)
        ) { result -> onResult(result) }
        currentLayout.addView(drawingView)

        return root
    }
}
