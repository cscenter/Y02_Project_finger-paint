package ru.cscenter.fingerpaint.ui.games.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.cscenter.fingerpaint.R

abstract class BaseGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        startGame()
    }

    fun startGame() = runGame(firstGame().getGame())

    abstract fun firstGame(): SingleGame

    fun showFragment(fragment: Fragment) = supportFragmentManager
        .beginTransaction()
        .replace(R.id.fragment_container, fragment)
        .addToBackStack(null)
        .commit()

    fun runGame(game: Game) = showFragment(game)

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

}
