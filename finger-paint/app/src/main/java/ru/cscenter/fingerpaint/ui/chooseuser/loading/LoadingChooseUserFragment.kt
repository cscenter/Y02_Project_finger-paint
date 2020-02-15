package ru.cscenter.fingerpaint.ui.chooseuser.loading

import android.app.Activity
import androidx.navigation.NavController
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.ui.chooseuser.base.BaseChooseUserFragment
import ru.cscenter.fingerpaint.ui.chooseuser.base.ChooseUserAdapter

class LoadingChooseUserFragment : BaseChooseUserFragment() {
    override fun getAdapter(
        activity: Activity,
        navController: NavController
    ) = ChooseUserAdapter(activity, R.layout.choose_user_loading_item)
    { view -> LoadingUserViewHolder(view, activity, navController, onChooseUser) }
}
