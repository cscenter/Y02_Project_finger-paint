package ru.cscenter.fingerpaint.ui.chooseuser.main

import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.navigation.NavController
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.ui.chooseuser.base.BaseUserViewHolder
import ru.cscenter.fingerpaint.ui.statistics.StatisticsFragmentArgs

class UserViewHolder(
    private val view: View,
    private val navController: NavController,
    private val onDeleteUser: (User) -> Unit
) : BaseUserViewHolder(view, navController) {
    private val statisticButton: ImageView = view.findViewById(R.id.choose_user_statistics_button)
    private val menuButton: ImageView = view.findViewById(R.id.menuButton)

    override fun bindUser(user: User) {
        super.bindUser(user)
        view.setOnClickListener {
            setUserAsCurrent(user.id)
            navController.popBackStack()
        }

        statisticButton.setOnClickListener { navigateToStatistics(user.id) }

        menuButton.setOnClickListener {
            val menu = PopupMenu(view.context, it)
            menu.inflate(R.menu.user_menu)
            menu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.item_set -> navigateToSetUser(user.id)
                    R.id.item_delete -> onDeleteUser(user)
                    else -> error("Invalid menu item.")
                }
                return@setOnMenuItemClickListener false
            }
            menu.show()
        }
    }

    private fun navigateToStatistics(userId: Int) = navController.navigate(
        R.id.nav_statistics,
        StatisticsFragmentArgs(userId).toBundle()
    )
}
