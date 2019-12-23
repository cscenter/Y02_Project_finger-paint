package ru.cscenter.fingerpaint.ui.chooseuser.loading

import android.app.Activity
import android.view.View
import androidx.navigation.NavController
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.ui.chooseuser.base.BaseUserViewHolder
import ru.cscenter.fingerpaint.ui.title.toMainActivity

class LoadingUserViewHolder(
    private val view: View,
    private val activity: Activity,
    navController: NavController
) : BaseUserViewHolder(view, navController) {

    override fun bindUser(user: User) {
        super.bindUser(user)
        view.setOnClickListener {
            setUserAsCurrent(user.id)
            toMainActivity(activity)
        }
    }
}