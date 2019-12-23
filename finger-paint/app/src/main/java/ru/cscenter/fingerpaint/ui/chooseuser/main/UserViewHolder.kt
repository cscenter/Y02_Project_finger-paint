package ru.cscenter.fingerpaint.ui.chooseuser.main

import android.view.View
import android.widget.Button
import androidx.navigation.NavController
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.ui.chooseuser.base.BaseUserViewHolder
import ru.cscenter.fingerpaint.ui.statistics.StatisticsFragmentArgs

class UserViewHolder(
    private val view: View,
    private val navController: NavController
) : BaseUserViewHolder(view, navController) {
    private val statisticButton: Button = view.findViewById(R.id.choose_user_statistics_button)

    override fun bindUser(user: User) {
        super.bindUser(user)
        view.setOnClickListener {
            setUserAsCurrent(user.id)
            navController.popBackStack()
        }

        statisticButton.setOnClickListener { navigateToStatistics(user.id) }
    }

    private fun navigateToStatistics(userId: Int) = navController.navigate(
        R.id.nav_statistics,
        StatisticsFragmentArgs(userId).toBundle()
    )
}