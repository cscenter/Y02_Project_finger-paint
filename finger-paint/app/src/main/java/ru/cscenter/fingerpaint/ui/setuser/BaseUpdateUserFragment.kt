package ru.cscenter.fingerpaint.ui.setuser

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.models.UsersModel

abstract class BaseUpdateUserFragment : Fragment() {

    abstract suspend fun updateUser(name: String, model: UsersModel): Boolean

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val usersModel: UsersModel by activityViewModels()
        val root = inflater.inflate(R.layout.fragment_set_user, container, false)
        val okButton: Button = root.findViewById(R.id.ok_button)

        val nameTextView: EditText = root.findViewById(R.id.text_edit_name)
        nameTextView.doOnTextChanged { text, _, _, _ ->
            nameTextView.error =
                if (text.isNullOrEmpty()) getString(R.string.empty_name_warning) else null
        }

        okButton.setOnClickListener {
            val name = nameTextView.text.toString()
            if (name.isEmpty()) {
                nameTextView.error = getString(R.string.empty_name_warning)
                return@setOnClickListener
            }

            GlobalScope.launch(Dispatchers.Main) {
                val success = updateUser(name, usersModel)
                if (!success) {
                    nameTextView.error = getString(R.string.user_exists_warning)
                    return@launch
                }

                hideKeyboard(it)
                findNavController().popBackStack()
            }
        }

        return root
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard(activity!!.window.decorView.rootView)
    }

    private fun hideKeyboard(view: View) =
        getInputMethodManager().hideSoftInputFromWindow(view.windowToken, 0)

    private fun getInputMethodManager() =
        context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
}
