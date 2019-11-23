package ru.cscenter.fingerpaint.ui.games

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        @Suppress("MoveVariableDeclarationIntoWhen")
        val type = intent.getSerializableExtra(getString(R.string.arg_game_type)) as GameType

        when (type) {
            GameType.SIMPLE_GAME_TYPE -> runGame(SimpleGame(onSimpleGameResult))
        }
    }

    private fun runGame(game: Game) = supportFragmentManager
        .beginTransaction()
        .replace(R.id.fragment_container, game)
        .addToBackStack(null)
        .commit()

    private val onSimpleGameResult = { result: Boolean ->
        val db = MainApplication.dbController
        val statistic = db.getCurrentUserStatistics()
        statistic?.let {
            it.figureChooseTotal++
            it.figureChooseSuccess += if (result) 1 else 0
            db.setStatistics(it)
        }
        Toast.makeText(this, "Your shore is ${statistic?.figureChooseSuccess}.", Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}

enum class GameType {
    SIMPLE_GAME_TYPE
}
