package ru.cscenter.fingerpaint.ui.games

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.ui.games.images.*

class GameActivity : AppCompatActivity() {

    private lateinit var type: GameType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        type = intent.getSerializableExtra(getString(R.string.arg_game_type)) as GameType

        startGame()
    }

    fun startGame() = when (type) {
        GameType.FIGURES_GAME -> runGame(getChooseFigureGame())
        GameType.LETTERS_1_GAME -> runGame(getChooseLetterColorGame())
        GameType.LETTERS_2_GAME -> runGame(getChooseLetterGame())
    }

    private fun showFragment(fragment: Fragment) = supportFragmentManager
        .beginTransaction()
        .replace(R.id.fragment_container, fragment)
        .addToBackStack(null)
        .commit()

    private fun runGame(game: Game) = showFragment(game)

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

    private fun getResultMessage(result: GameResult) = when (result) {
        GameResult.SUCCESS -> getString(R.string.success_message)
        GameResult.FAIL -> getString(R.string.fail_message)
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

            val nextGame = nextGame()
            if (result == GameResult.SUCCESS && nextGame != null) {
                runGame(nextGame)
            } else {
                val message = getResultMessage(result)
                showFragment(ResultFragment(message))
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
