package ru.cscenter.fingerpaint.ui.games

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import ru.cscenter.fingerpaint.R

class DrawingGame(
    private val question: String,
    private val imageSupplier: (width: Int, height: Int) -> Bitmap,
    private val backgroundImageSupplier: (width: Int, height: Int) -> Bitmap,
    private val paintColor: Int,
    private val thresholds: Pair<Float, Float>,
    private val callback: GameActivity.GameCallback
) : Game() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_drawing_game, container, false)

        val questionView: TextView = root.findViewById(R.id.question)

        questionView.text = question

        val goodProgress: ProgressBar = root.findViewById(R.id.good_progress)
        val badProgress: ProgressBar = root.findViewById(R.id.bad_progress)

        val currentLayout: LinearLayout = root.findViewById(R.id.drawing_layout)

        val drawingView = DrawingView(
            context,
            imageSupplier,
            backgroundImageSupplier,
            thresholds,
            paintColor,
            Pair(goodProgress, badProgress),
            callback
        )
        currentLayout.addView(drawingView)

        return root
    }
}
