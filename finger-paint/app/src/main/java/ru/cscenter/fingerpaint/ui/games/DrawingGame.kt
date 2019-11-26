package ru.cscenter.fingerpaint.ui.games

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import ru.cscenter.fingerpaint.R

class DrawingGame(
    private val question: String,
    private val image: Bitmap,
    private val thresholdSuccess: Float,
    private val thresholdFail: Float,
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

        val currentLayout: LinearLayout = root.findViewById(R.id.drawing_layout)

        val drawingView = DrawingView(
            context,
            image,
            thresholdSuccess,
            thresholdFail,
            callback
        )
        drawingView.setImageBitmap(image)

        currentLayout.addView(drawingView)

        return root
    }
}
