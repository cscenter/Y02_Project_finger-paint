package ru.cscenter.fingerpaint.ui.setuser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.cscenter.fingerpaint.R

class SetUserFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_set_user, container, false)
        // get arguments from navigation
        val okButton: Button = root.findViewById(R.id.ok_button)
        okButton.setOnClickListener {
            val navController = findNavController()
            // pass result
            navController.popBackStack()
        }

        return root
    }
}
