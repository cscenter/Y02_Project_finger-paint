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
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.cscenter.fingerpaint.R

abstract class BaseUpdateUserFragment : Fragment() {

    abstract fun updateUser(name: String, onResult: (Boolean) -> Unit)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

            updateUser(name) { success ->
                if (!success) {
                    nameTextView.error = getString(R.string.user_exists_warning)
                    Snackbar.make(requireView(), "Operation failed", Snackbar.LENGTH_LONG)
                    return@updateUser
                }

                GlobalScope.launch(Dispatchers.Main) {
                    hideKeyboard(it)
                    findNavController().popBackStack()
                }
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
