package ru.cscenter.fingerpaint.ui.setuser

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.cscenter.fingerpaint.ActivitySetUserListenerContainer
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.User


class SetUserFragment : Fragment() {

    private var type: SetUserType? = null
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_set_user, container, false)

        val errorText = getString(R.string.empty_name_warning)
        val nameTextView: EditText = root.findViewById(R.id.text_edit_name)
        nameTextView.doOnTextChanged { text, _, _, _ ->
            nameTextView.error = if (text.isNullOrEmpty()) errorText else null
        }

        val dbController = MainApplication.dbController

        arguments?.let {
            val safeArgs = SetUserFragmentArgs.fromBundle(it)
            type = safeArgs.setUserType
            if (type == SetUserType.UPDATE_USER) {
                val userId = safeArgs.userId
                user = dbController.getUser(userId)
                user?.let { u -> nameTextView.setText(u.toString()) }
            }
        }

        if (type == null || (type == SetUserType.UPDATE_USER && user == null)) {
            Log.e("FingerPaint.SetUser", " Illegal arguments provided.")
            onDestroy()
        }

        val okButton: Button = root.findViewById(R.id.ok_button)
        okButton.setOnClickListener {
            val name = nameTextView.text.toString()
            if (name.isEmpty()) {
                nameTextView.error = errorText
                return@setOnClickListener
            }

            when (type) {
                SetUserType.UPDATE_USER -> {
                    user?.let { user ->
                        user.name = name
                        val updateSuccess = dbController.setUser(user)
                        if (!updateSuccess) {
                            nameTextView.error = getString(R.string.user_exists_warning)
                            return@setOnClickListener
                        }
                    }
                }
                SetUserType.INSERT_USER -> {
                    val insertSuccess = dbController.insertUser(name)
                    if (!insertSuccess) {
                        nameTextView.error = getString(R.string.user_exists_warning)
                        return@setOnClickListener
                    }
                }
            }

            hideKeyboard(it)

            navigateBackWithResult(type!!, dbController.getUserId(name))
        }

        return root
    }

    private fun hideKeyboard(view: View) =
        getInputMethodManager().hideSoftInputFromWindow(view.windowToken, 0)

    private fun getInputMethodManager() =
        context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    private fun navigateBackWithResult(type: SetUserType, userId: Int) {
        val fragmentContainer = activity as ActivitySetUserListenerContainer?
        val fragment = fragmentContainer?.getSetUserListener()
        fragment?.onSetUser(type, userId)
        findNavController().popBackStack()
    }
}

enum class SetUserType {
    INSERT_USER,
    UPDATE_USER
}
