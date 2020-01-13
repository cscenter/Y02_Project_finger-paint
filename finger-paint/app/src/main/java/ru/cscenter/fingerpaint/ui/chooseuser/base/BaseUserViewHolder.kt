package ru.cscenter.fingerpaint.ui.chooseuser.base

import android.view.View
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.ui.setuser.SetUserFragmentArgs
import ru.cscenter.fingerpaint.ui.setuser.SetUserType

open class BaseUserViewHolder(
    private val view: View,
    private val navController: NavController
) : RecyclerView.ViewHolder(view) {
    private val nameView: TextView = view.findViewById(R.id.choose_user_name_text_view)
    open fun bindUser(user: User) {
        nameView.text = user.name

        view.setOnLongClickListener {
            navigateToSetUser(user.id)
            true
        }
    }

    protected fun navigateToSetUser(userId: Int) = navController.navigate(
        R.id.nav_set_user,
        SetUserFragmentArgs(SetUserType.UPDATE_USER, userId).toBundle()
    )

    protected fun setUserAsCurrent(userId: Int) =
        MainApplication.dbController.setCurrentUser(userId)
}
