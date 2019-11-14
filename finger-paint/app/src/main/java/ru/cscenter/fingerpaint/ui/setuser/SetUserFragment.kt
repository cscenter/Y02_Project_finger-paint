package ru.cscenter.fingerpaint.ui.setuser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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

        val nameTextView: EditText = root.findViewById(R.id.text_edit_name)
        val surnameTextView: EditText = root.findViewById(R.id.text_edit_surname)

        val okButton: Button = root.findViewById(R.id.ok_button)
        okButton.setOnClickListener {
            val name = nameTextView.text.toString()
            val surname = surnameTextView.text.toString()
            if (name.isEmpty() or surname.isEmpty()) {
                Toast.makeText(context, getString(R.string.empty_name_warning), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            MainApplication.dbController.insertUser(name, surname)

            val navController = findNavController()
            // pass result
            navController.popBackStack()
        }

        return root
    }
}
