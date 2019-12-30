package ru.cscenter.fingerpaint.ui.chooseuser.loading

import android.app.Activity
import androidx.navigation.NavController
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.ui.chooseuser.base.BaseChooseUserAdapter
import ru.cscenter.fingerpaint.ui.chooseuser.base.BaseChooseUserFragment

class LoadingChooseUserFragment : BaseChooseUserFragment() {
    override fun getAdapter(
        activity: Activity,
        users: MutableList<User>,
        navController: NavController
    ) = BaseChooseUserAdapter(activity, users, R.layout.choose_user_loading_item)
    { view -> LoadingUserViewHolder(view, activity, navController) }
}