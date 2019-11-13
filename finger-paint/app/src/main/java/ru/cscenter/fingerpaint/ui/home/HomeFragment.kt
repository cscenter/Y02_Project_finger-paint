package ru.cscenter.fingerpaint.ui.home

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

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val currentNameTextView: TextView = root.findViewById(R.id.current_name_text_view)
        currentNameTextView.text = homeViewModel.currentName

        val statisticsButton: Button = root.findViewById(R.id.statistics_button)
        statisticsButton.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.nav_statistics)
        }

        return root
    }
}
