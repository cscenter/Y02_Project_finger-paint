package ru.cscenter.fingerpaint.ui.choose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.ui.title.toMainActivity

class ChooseFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_choose, container, false)
        val listView: ListView = root.findViewById(R.id.users_list)

        val usersList = listOf("Mike", "Tom", "Frederic", "Antonio")
        val adapter = ArrayAdapter<String>(
            container!!.context,
            android.R.layout.simple_list_item_1,
            usersList
        )

        val navController = findNavController(this)

        listView.onItemClickListener = AdapterView.OnItemClickListener  { _, _, _, _ ->
            // write as current user
            if (!MainApplication.hasCurrentUser) {
                MainApplication.hasCurrentUser = true
                toMainActivity(activity!!)
            } else {
                navController.popBackStack()
            }
        }

        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, i: Int, _ ->
            Toast.makeText(context, "Setting ${usersList[i]}", Toast.LENGTH_LONG).show()
            // pass arguments
            navController.navigate(R.id.nav_set_user)
            true
        }

        listView.adapter = adapter

        val addUserButton: Button = root.findViewById(R.id.add_user_button)
        addUserButton.setOnClickListener {
            navController.navigate(R.id.nav_set_user)
        }

        return root
    }
}
