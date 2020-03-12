package ru.cscenter.fingerpaint.ui.chooseuser.main

import android.app.Activity
import androidx.navigation.NavController
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.ui.chooseuser.base.BaseChooseUserFragment
import ru.cscenter.fingerpaint.ui.chooseuser.base.ChooseUserAdapter
import ru.cscenter.fingerpaint.ui.statistics.navigateToStatistics

class ChooseUserFragment : BaseChooseUserFragment() {
    override fun getAdapter(
        activity: Activity,
        navController: NavController
    ) = ChooseUserAdapter(activity, R.layout.choose_user_item) { view ->
        UserViewHolder(
            view,
            navController,
            { user -> onDeleteUser(user, activity) },
            { user -> navigateToStatistics(this, user) },
            onChooseUser
        )
    }

    private fun onDeleteUser(user: User, activity: Activity) {
        MainApplication.synchronizeController.deleteUser(user, activity) { }
    }
}
