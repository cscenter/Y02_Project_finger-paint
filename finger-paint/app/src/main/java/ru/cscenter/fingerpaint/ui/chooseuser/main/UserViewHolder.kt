package ru.cscenter.fingerpaint.ui.chooseuser.main

import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.navigation.NavController
import kotlinx.coroutines.Job
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.db.UserStatus
import ru.cscenter.fingerpaint.ui.chooseuser.base.BaseUserViewHolder

class UserViewHolder(
    private val view: View,
    private val navController: NavController,
    private val onDeleteUser: (User) -> Unit,
    private val onUserStatistics: (User) -> Unit,
    private val onChooseUser: (User) -> Job
) : BaseUserViewHolder(view, navController) {
    private val statisticButton: ImageView = view.findViewById(R.id.choose_user_statistics_button)
    private val menuButton: ImageView = view.findViewById(R.id.menuButton)

    override fun bindUser(user: User) {
        super.bindUser(user)
        view.setOnClickListener {
            onChooseUser(user)
            navController.popBackStack()
        }

        statisticButton.setOnClickListener { onUserStatistics(user) }

        menuButton.setOnClickListener {
            val menu = PopupMenu(view.context, it)
            val resource = if (MainApplication.settings.isOnlineState() || user.status == UserStatus.NEW)
                R.menu.user_menu_online else R.menu.user_menu_offline
            menu.inflate(resource)
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
}
