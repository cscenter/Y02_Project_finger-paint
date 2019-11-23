package ru.cscenter.fingerpaint.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.ui.games.GameActivity
import ru.cscenter.fingerpaint.ui.games.GameType

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var currentNameTextView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        currentNameTextView = root.findViewById(R.id.current_name_text_view)
        currentNameTextView!!.text = homeViewModel.currentName()

        val statisticsButton: Button = root.findViewById(R.id.statistics_button)
        statisticsButton.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.nav_statistics)
        }

        val figureButton: Button = root.findViewById(R.id.figure_game_button)
        figureButton.setOnClickListener {
            runGame(GameType.SIMPLE_GAME_TYPE)
        }

        val letter1Button: Button = root.findViewById(R.id.letter1_game_button)
        letter1Button.setOnClickListener {
            runGame(GameType.SIMPLE_GAME_TYPE)
        }

        val letter2Button: Button = root.findViewById(R.id.letter2_game_button)
        letter2Button.setOnClickListener {
            runGame(GameType.SIMPLE_GAME_TYPE)
        }

        return root
    }

    private fun runGame(type: GameType) {
        val intent = Intent(activity, GameActivity::class.java)
        intent.putExtra(getString(R.string.arg_game_type), type)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        currentNameTextView?.text = homeViewModel.currentName()
    }
}
