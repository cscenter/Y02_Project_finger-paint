package ru.cscenter.fingerpaint.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.models.CurrentUserModel
import ru.cscenter.fingerpaint.ui.games.*
import ru.cscenter.fingerpaint.ui.games.base.BaseGameActivity
import ru.cscenter.fingerpaint.ui.statistics.navigateToStatistics

class HomeFragment : Fragment() {

    override fun onResume() {
        super.onResume()
        GlobalScope.launch(Dispatchers.IO) {
            val user = MainApplication.dbController.getCurrentUser()
            MainApplication.synchronizeController.checkUserExists(user?.id, activity!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val currentUserModel: CurrentUserModel by activityViewModels()
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val extraGamesButton: Button = root.findViewById(R.id.extra_game_button)

        val networkStateIcon: ImageView = root.findViewById(R.id.network_state_icon)
        MainApplication.settings.isOnline().observe(viewLifecycleOwner, Observer { isOnline ->
            val resource = if (isOnline) R.drawable.ic_online_icon else R.drawable.ic_offline_icon
            networkStateIcon.setImageResource(resource)
            extraGamesButton.visibility = if (isOnline) View.VISIBLE else View.GONE
        })

        val currentNameTextView: TextView = root.findViewById(R.id.current_name_text_view)
        currentUserModel.currentUser.observe(viewLifecycleOwner, Observer { user ->
            currentNameTextView.text = user?.toString() ?: ""
        })

        val statisticsButton: ImageView = root.findViewById(R.id.statistics_button)
        statisticsButton.setOnClickListener {
            val user = currentUserModel.currentUser.value
            if (user == null) {
                Log.e(getString(R.string.app_name), "Current user id is null!")
                return@setOnClickListener
            }
            navigateToStatistics(this, user)
        }

        extraGamesButton.setOnClickListener {
            findNavController().navigate(R.id.nav_tasks)
        }

        root.findViewById<Button>(R.id.only_choose_figure_button).setOnClickListener {
            runOnlyOneGame(OnlyOneGameType.CHOOSE_FIGURE_GAME)
        }
        root.findViewById<Button>(R.id.only_choose_color_button).setOnClickListener {
            runOnlyOneGame(OnlyOneGameType.CHOOSE_COLOR_GAME)
        }
        root.findViewById<Button>(R.id.only_choose_letter_button).setOnClickListener {
            runOnlyOneGame(OnlyOneGameType.CHOOSE_LETTER_GAME)
        }
        root.findViewById<Button>(R.id.only_draw_figure_button).setOnClickListener {
            runOnlyOneGame(OnlyOneGameType.DRAW_FIGURE_GAME)
        }
        root.findViewById<Button>(R.id.only_draw_letter_button).setOnClickListener {
            runOnlyOneGame(OnlyOneGameType.DRAW_LETTER_GAME)
        }

        val figureButton: Button = root.findViewById(R.id.figure_game_button)
        figureButton.setOnClickListener {
            runGame(FiguresGameActivity::class.java)
        }

        val letter1Button: Button = root.findViewById(R.id.letter1_game_button)
        letter1Button.setOnClickListener {
            runGame(Letters1GameActivity::class.java)
        }

        val letter2Button: Button = root.findViewById(R.id.letter2_game_button)
        letter2Button.setOnClickListener {
            runGame(Letters2GameActivity::class.java)
        }

        return root
    }

    private fun runOnlyOneGame(type: OnlyOneGameType) {
        val intent = Intent(activity, OnlyOneGameActivity::class.java)
        intent.putExtra(ARG_ONLY_ONE_GAME_TYPE, type)
        startActivity(intent)
    }

    private fun runGame(gameType: Class<out BaseGameActivity>) {
        val intent = Intent(activity, gameType)
        startActivity(intent)
    }
}
