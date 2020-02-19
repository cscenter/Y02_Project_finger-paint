package ru.cscenter.fingerpaint.ui.setuser

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.viewModels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.models.UsersModel


class SetUserFragment : BaseUpdateUserFragment() {

    private lateinit var user: User

    override fun updateUser(name: String, onResult: (Boolean) -> Unit) {
        user.name = name
        MainApplication.synchronizeController.updateUser(user, onResult)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)!!
        val nameTextView: EditText = root.findViewById(R.id.text_edit_name)
        val usersModel: UsersModel by viewModels()

        val args = arguments ?: return onErrorArgs()
        val safeArgs = SetUserFragmentArgs.fromBundle(args)
        val userId = safeArgs.userId

        GlobalScope.launch(Dispatchers.Main) {
            val user = usersModel.getUser(userId)
            if (user == null) {
                onErrorArgs()
                return@launch
            }
            user.let { u -> nameTextView.setText(u.toString()) }
            this@SetUserFragment.user = user
        }

        return root
    }

    private fun onErrorArgs(): View? {
        Log.e("FingerPaint.SetUser", " Illegal arguments provided.")
        onDestroy()
        return null
    }
}
