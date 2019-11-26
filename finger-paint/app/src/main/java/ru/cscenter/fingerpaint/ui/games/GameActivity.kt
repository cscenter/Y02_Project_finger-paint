package ru.cscenter.fingerpaint.ui.games

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic

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
        ChooseGameFragment(
            "Choose One",
            R.drawable.image_1,
            R.drawable.image_2,
            ChooseFigureGameCallback()
        )

    private fun getDrawingGame(): Game =
        DrawingGame(
            "Replace Black to Yellow by your finger",
            drawableToBitmap(ResourcesCompat.getDrawable(resources, R.drawable.image_2, null)!!),
            0.7f,
            0.03f,
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

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        val bitmap: Bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}

enum class GameType {
    FIGURES_GAME,
    LETTERS_1_GAME,
    LETTERS_2_GAME
}
