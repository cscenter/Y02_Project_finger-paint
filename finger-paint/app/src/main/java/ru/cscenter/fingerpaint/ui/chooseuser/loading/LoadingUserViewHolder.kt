package ru.cscenter.fingerpaint.ui.chooseuser.loading

import android.app.Activity
import android.view.View
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.cscenter.fingerpaint.MainActivity
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.ui.chooseuser.base.BaseUserViewHolder
import ru.cscenter.fingerpaint.ui.title.toActivity

class LoadingUserViewHolder(
    private val view: View,
    private val activity: Activity,
    navController: NavController,
    private val onChooseUser: (User) -> Job
) : BaseUserViewHolder(view, navController) {

    override fun bindUser(user: User) {
        super.bindUser(user)
        view.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                onChooseUser(user).join()
                toActivity(activity, MainActivity::class.java)
            }
        }
    }
}
