package ru.cscenter.fingerpaint.ui.games

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.ui.games.images.*

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        @Suppress("MoveVariableDeclarationIntoWhen")
        val type = intent.getSerializableExtra(getString(R.string.arg_game_type)) as GameType

        when (type) {
            GameType.FIGURES_GAME -> runGame(getChooseFigureGame())
            GameType.LETTERS_1_GAME -> runGame(getChooseLetterColorGame())
            GameType.LETTERS_2_GAME -> runGame(getChooseLetterGame())
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
            "Choose rectangle",
            correctImageSupplier = getFigureImage(FigureType.RECTANGLE, Color.BLUE, false),
            incorrectImageSupplier = getFigureImage(FigureType.SQUARE, Color.BLUE, false),
            callback = ChooseFigureGameCallback()
        )

    private fun getChooseFigureColorGame(): Game =
        ChooseGame(
            "Choose red",
            correctImageSupplier = getFigureImage(FigureType.TRIANGLE, Color.RED, true),
            incorrectImageSupplier = getFigureImage(FigureType.CIRCLE, Color.BLUE, true),
            callback = ChooseFigureColorGameCallback()
        )

    private fun getDrawingFigureGame(): Game =
        DrawingGame(
            "Replace Black to Yellow by your finger",
            getFigureImageCompressed(FigureType.RECTANGLE),
            getFigureImageCompressed(FigureType.RECTANGLE, Color.MAGENTA, false),
            figureThresholds,
            DrawingFigureGameCallback()
        )

    private fun getChooseLetterGame(): Game =
        ChooseGame(
            "Choose Ы",
            correctImageSupplier = getLetterImage("Ы"),
            incorrectImageSupplier = getLetterImage("И"),
            callback = ChooseLetterGameCallback()
        )

    private fun getChooseLetterColorGame(): Game =
        ChooseGame(
            "Choose red",
            correctImageSupplier = getLetterImage("Ы", color = Color.RED),
            incorrectImageSupplier = getLetterImage("Ы", color = Color.GREEN),
            callback = ChooseLetterColorGameCallback()
        )

    private fun getDrawingLetterGame(): Game =
        DrawingGame(
            "Replace Black to Yellow by your finger",
            getLetterImageCompressed("Ы"),
            getLetterImageCompressed("Ы"),
            letterThresholds,
            DrawingLetterGameCallback()
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
            Toast.makeText(
                applicationContext,
                if (result == GameResult.SUCCESS) "Well done!" else "Try again",
                Toast.LENGTH_LONG
            ).show()
            when (result) {
                GameResult.SUCCESS -> nextGame()?.let { runGame(it) } ?: finish()
                GameResult.FAIL -> finish()
            }
        }
    }

    inner class ChooseFigureGameCallback : GameCallback() {
        override fun nextGame(): Game? = getChooseFigureColorGame()

        override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
            statistic.figureChooseTotal++
            statistic.figureChooseSuccess += result.toInt()
            return statistic
        }
    }

    inner class ChooseFigureColorGameCallback : GameCallback() {
        override fun nextGame(): Game? = getDrawingFigureGame()

        override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
            statistic.figureColorChooseTotal++
            statistic.figureColorChooseSuccess += result.toInt()
            return statistic
        }
    }

    inner class DrawingFigureGameCallback : GameCallback() {
        override fun nextGame(): Game? = null

        override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
            statistic.drawingTotal++
            statistic.drawingSuccess += result.toInt()
            return statistic
        }
    }

    inner class ChooseLetterGameCallback : GameCallback() {
        override fun nextGame(): Game? = getChooseLetterColorGame()

        override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
            statistic.letterChooseTotal++
            statistic.letterChooseSuccess += result.toInt()
            return statistic
        }
    }

    inner class ChooseLetterColorGameCallback : GameCallback() {
        override fun nextGame(): Game? = getDrawingLetterGame()

        override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
            statistic.letterColorChooseTotal++
            statistic.letterColorChooseSuccess += result.toInt()
            return statistic
        }
    }

    inner class DrawingLetterGameCallback : GameCallback() {
        override fun nextGame(): Game? = null

        override fun updateStatistics(statistic: Statistic, result: GameResult): Statistic {
            statistic.contouringTotal++
            statistic.contouringSuccess += result.toInt()
            return statistic
        }
    }

    companion object {
        private val figureThresholds = Pair(0.8f, 0.1f)
        private val letterThresholds = Pair(0.75f, 0.1f)
    }

}

enum class GameType {
    FIGURES_GAME,
    LETTERS_1_GAME,
    LETTERS_2_GAME
}
