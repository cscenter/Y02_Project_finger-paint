package ru.cscenter.fingerpaint.ui.setuser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R

class SetUserFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_set_user, container, false)
        // get arguments from navigation

        val errorText = getString(R.string.empty_name_warning)
        val nameTextView: EditText = root.findViewById(R.id.text_edit_name)
        nameTextView.doOnTextChanged { text, _, _, _ ->
            nameTextView.error = if (text.isNullOrEmpty()) errorText else null
        }

        val okButton: Button = root.findViewById(R.id.ok_button)
        okButton.setOnClickListener {
            val name = nameTextView.text.toString()
            if (name.isEmpty()) {
                nameTextView.error = errorText
                return@setOnClickListener
            }

            val insertSuccess = MainApplication.dbController.insertUser(name)
            if (!insertSuccess) {
                nameTextView.error = getString(R.string.user_exists_warning)
                return@setOnClickListener
            }

            val navController = findNavController()
            // pass result
            navController.popBackStack()
        }

        return root
    }
}
