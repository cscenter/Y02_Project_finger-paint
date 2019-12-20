package ru.cscenter.fingerpaint.ui.games

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

    private fun getChooseFigureGame(): Game {
        val (correctColor, incorrectColor) = figuresRandom.getRandomPair()
        val color = colorsRandom.getRandomValue()
        return ChooseGame(
            getChooseFigureTask(correctColor),
            correctImageSupplier = getFigureImage(correctColor, color, false),
            incorrectImageSupplier = getFigureImage(incorrectColor, color, false),
            callback = ChooseFigureGameCallback()
        )
    }

    private fun getChooseFigureColorGame(): Game {
        val (figure1, figure2) = figuresRandom.getRandomPair()
        val (correctColor, incorrectColor) = colorsRandom.getRandomPair()
        return ChooseGame(
            getChooseFigureColorTask(correctColor),
            correctImageSupplier = getFigureImage(figure1, correctColor, true),
            incorrectImageSupplier = getFigureImage(figure2, incorrectColor, true),
            callback = ChooseFigureColorGameCallback()
        )
    }

    private fun getDrawingFigureGame(): Game {
        val figure = figuresRandom.getRandomValue()
        val color = colorsRandom.getRandomValue()
        return DrawingGame(
            getDrawFigureTask(figure),
            getFigureImageCompressed(figure),
            getFigureImageCompressed(figure, color, false),
            color,
            figureThresholds,
            DrawingFigureGameCallback()
        )
    }

    private fun getChooseLetterGame(): Game {
        val (correctLetter, incorrectLetter) = lettersRandom.getRandomPair()
        return ChooseGame(
            getChooseLetterTask(correctLetter),
            correctImageSupplier = getLetterImage(correctLetter),
            incorrectImageSupplier = getLetterImage(incorrectLetter),
            callback = ChooseLetterGameCallback()
        )
    }

    private fun getChooseLetterColorGame(): Game {
        val (letter1, letter2) = lettersRandom.getRandomPair()
        val (correctColor, incorrectColor) = colorsRandom.getRandomPair()
        return ChooseGame(
            getChooseLetterColorTask(correctColor),
            correctImageSupplier = getLetterImage(letter1, correctColor),
            incorrectImageSupplier = getLetterImage(letter2, incorrectColor),
            callback = ChooseLetterColorGameCallback()
        )
    }

    private fun getDrawingLetterGame(): Game {
        val letter = lettersRandom.getRandomValue()
        val color = colorsRandom.getRandomValue()
        return DrawingGame(
            getDrawLetterTask(),
            getLetterImageCompressed(letter),
            getLetterImageCompressed(letter),
            color,
            letterThresholds,
            DrawingLetterGameCallback()
        )
    }

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
