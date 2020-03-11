package ru.cscenter.fingerpaint.ui.games.base

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.RecyclerView
import com.divyanshu.draw.widget.DrawView
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.service.AudioController

abstract class DrawingGame(private val config: Config, gameActivity: BaseGameActivity) :
    Game(gameActivity) {

    data class Config(
        val question: String,
        val imageSupplier: (width: Int, height: Int) -> Bitmap,
        val backgroundImageSupplier: (width: Int, height: Int) -> Bitmap,
        val paintColor: Int,
        val thresholds: Pair<Float, Float>
    )

    private lateinit var audioController: AudioController
    private lateinit var drawView: DrawView
    private lateinit var widthBar: SeekBar
    private lateinit var colorsList: RecyclerView
    private lateinit var drawChecker: DrawChecker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_drawing_game, container, false)

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

        val goodProgress: ProgressBar = root.findViewById(R.id.good_progress)
        val badProgress: ProgressBar = root.findViewById(R.id.bad_progress)

        drawView = root.findViewById(R.id.draw_view)
        widthBar = root.findViewById(R.id.width_bar)
        colorsList = root.findViewById(R.id.colors_list)

        drawView.doOnPreDraw {
            val drawable = BitmapDrawable(resources, config.backgroundImageSupplier(it.width, it.height))
            it.background = drawable
            drawChecker = DrawChecker(
                context!!,
                drawView,
                config.imageSupplier(it.width, it.height),
                config.thresholds,
                Pair(goodProgress, badProgress)
            ) { result -> onResult(result) }

        }

        setUpDrawTools(root)
        colorSelector()
        setPaintWidth()

        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        audioController.shutdown()
        drawChecker.cancel()

    }

    companion object {
        private val ZERO_SIZE = 0.toPx
        private val MENU_SIZE = 64.toPx
        private val Int.toPx: Float
            get() = (this * Resources.getSystem().displayMetrics.density)
    }

    private fun setShowBar(tools: View, button: View, bar: View) {
        button.setOnClickListener {
            if (tools.translationY == MENU_SIZE) {
                toggleDrawTools(tools, true)
            } else if (tools.translationY == ZERO_SIZE && bar.visibility == View.VISIBLE) {
                toggleDrawTools(tools, false)
            }
            widthBar.visibility = if (bar === widthBar) View.VISIBLE else View.GONE
            colorsList.visibility = if (bar === colorsList) View.VISIBLE else View.GONE
        }
    }

    private fun setUpDrawTools(root: View) {
        val tools: View = root.findViewById(R.id.tools)
        val widthButton: ImageView = root.findViewById(R.id.width_button)
        val colorButton: ImageView = root.findViewById(R.id.color_button)
        val eraserButton: ImageView = root.findViewById(R.id.eraser_button)
        val undoButton: ImageView = root.findViewById(R.id.undo_button)
        val redoButton: ImageView = root.findViewById(R.id.redo_button)
        setShowBar(tools, widthButton, widthBar)
        setShowBar(tools, colorButton, colorsList)

        eraserButton.setOnClickListener {
            drawView.clearCanvas()
            toggleDrawTools(tools, false)
        }

        undoButton.setOnClickListener {
            drawView.undo()
            toggleDrawTools(tools, false)
        }

        redoButton.setOnClickListener {
            drawView.redo()
            toggleDrawTools(tools, false)
        }
    }

    private fun toggleDrawTools(view: View, showView: Boolean = true) {
        val y = if (showView) ZERO_SIZE else MENU_SIZE
        view.animate().translationY(y)
    }

    private fun colorSelector() {
        colorsList.adapter = ColorsAdapter(context!!, config.paintColor) {
            drawView.setColor(it)
        }
        drawView.setColor(config.paintColor)
    }

    private fun setPaintWidth() {
        widthBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                drawView.setStrokeWidth(progress.toFloat())
            }
        })
        widthBar.progress = 50
    }

}
