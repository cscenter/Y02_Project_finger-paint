package ru.cscenter.fingerpaint.ui.games

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.cscenter.fingerpaint.R

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        @Suppress("MoveVariableDeclarationIntoWhen")
        val type = intent.getSerializableExtra(getString(R.string.arg_game_type)) as GameType

        when (type) {
            GameType.FIGURES_GAME -> runGame(getChooseFigureGame())
            GameType.LETTERS_1_GAME -> finish()
            GameType.LETTERS_2_GAME -> finish()
        }
    }

    private fun runGame(game: Game) = supportFragmentManager
        .beginTransaction()
        .replace(R.id.fragment_container, game)
        .addToBackStack(null)
        .commit()

    private val onChooseGameResult = { result: Boolean ->
        Toast.makeText(this, "You ${if (result) "win" else "loose"}!!", Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun getChooseFigureGame(): Game =
        ChooseGameFragment(
            "CHOOSE QUESTION(correct answer is one)",
            R.drawable.image_1,
            R.drawable.image_2,
            onChooseGameResult
        )
}

enum class GameType {
    FIGURES_GAME,
    LETTERS_1_GAME,
    LETTERS_2_GAME
}
