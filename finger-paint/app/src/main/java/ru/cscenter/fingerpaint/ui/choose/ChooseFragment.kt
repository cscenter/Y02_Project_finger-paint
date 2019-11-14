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
import ru.cscenter.fingerpaint.db.UserName
import ru.cscenter.fingerpaint.ui.title.toMainActivity

class ChooseFragment : Fragment() {

    private val usersList = MainApplication.dbController.getAllNames().toMutableList()
    private lateinit var adapter: ArrayAdapter<UserName>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_choose, container, false)
        val listView: ListView = root.findViewById(R.id.users_list)

        val navController = findNavController(this)

        listView.onItemClickListener = AdapterView.OnItemClickListener  { _, _, index, _ ->
            MainApplication.dbController.setCurrentUser(usersList[index].id)
            if (MainApplication.isLoading) {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, usersList)
    }

    override fun onResume() {
        super.onResume()
        usersList.clear()
        usersList.addAll(MainApplication.dbController.getAllNames())
        adapter.notifyDataSetChanged()
    }
}
