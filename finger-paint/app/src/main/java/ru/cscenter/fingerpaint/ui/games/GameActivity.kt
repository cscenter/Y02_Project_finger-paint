package ru.cscenter.fingerpaint.ui.games

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import kotlin.math.min

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        @Suppress("MoveVariableDeclarationIntoWhen")
        val type = intent.getSerializableExtra(getString(R.string.arg_game_type)) as GameType

        when (type) {
            GameType.FIGURES_GAME -> runGame(getChooseFigureGame())
            GameType.LETTERS_1_GAME -> runGame(getDrawingGame())
            GameType.LETTERS_2_GAME -> finish()
        }
    }

    private fun runGame(game: Game) = supportFragmentManager
        .beginTransaction()
        .replace(R.id.fragment_container, game)
        .addToBackStack(null)
        .commit()

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun getChooseFigureGame(): Game =
        ChooseGame(
            "Choose queue",
            { width, height ->
                Images.getTextImageBitmap(
                    "Q",
                    width,
                    height
                )
            }, // correct choice
            { width, height ->
                Images.getFigureImageBitmap(
                    FigureType.CIRCLE,
                    width,
                    height,
                    min(width, height) / 50f, /* in px */
                    true,
                    Color.BLUE
                )
            }, // incorrect choice
            ChooseFigureGameCallback()
        )

    private fun getDrawingGame(): Game =
        DrawingGame(
            "Replace Black to Yellow by your finger",
            { width, height ->
                Images.getFigureImageBitmap(
                    FigureType.RECTANGLE,
                    width / 8, // for speed up
                    height / 8, // for speed up
                    min(width, height) / 8 / 50f, /* in px */
                    true,
                    Color.BLACK
                )
            }, // black-white image with good-bad pixels
            { width, height ->
                Images.getFigureImageBitmap(
                    FigureType.RECTANGLE,
                    width / 8, // for speed up
                    height / 8, // for speed up
                    min(width, height) / 8 / 50f /* in px */,
                    false,
                    Color.MAGENTA
                )
            }, // background image. User draw onto it
            Pair(0.8f, 0.1f),
            DrawingGameCallback()
        )


    abstract inner class GameCallback {
        private val db = MainApplication.dbController

        abstract fun updateStatistics(statistic: Statistic, result: GameResult): Statistic
        abstract fun nextGame(): Game?
        open fun onResult(result: GameResult) {
            supportFragmentManager.popBackStack()
            db.getCurrentUserStatistics()?.let {
                val statistic = updateStatistics(it, result)
                db.setStatistics(statistic)
            }
            when (result) {
                GameResult.SUCCESS -> nextGame()?.let { runGame(it) } ?: finish()
                GameResult.FAIL -> finish()
            }
        }
    }

    inner class ChooseFigureGameCallback : GameCallback() {
        override fun nextGame(): Game? {
            return null
        }

        override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
            statistic.figureChooseTotal++
            statistic.figureChooseSuccess += if (result == GameResult.SUCCESS) 1 else 0
            return statistic
        }
    }

    inner class DrawingGameCallback : GameCallback() {
        override fun nextGame(): Game? {
            return null
        }

        override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
            statistic.drawingTotal++
            statistic.drawingSuccess += if (result == GameResult.SUCCESS) 1 else 0
            return statistic
        }

        override fun onResult(result: GameResult) {
            Toast.makeText(
                applicationContext,
                if (result == GameResult.SUCCESS) "Your'e good" else "Your'e failed",
                Toast.LENGTH_LONG
            ).show()
            super.onResult(result)
        }
    }
}

enum class GameType {
    FIGURES_GAME,
    LETTERS_1_GAME,
    LETTERS_2_GAME
}
