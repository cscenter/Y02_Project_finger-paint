package ru.cscenter.fingerpaint.ui.chooseuser

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import ru.cscenter.fingerpaint.ActivitySetUserListenerContainer
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.ui.setuser.SetUserFragmentArgs
import ru.cscenter.fingerpaint.ui.setuser.SetUserListener
import ru.cscenter.fingerpaint.ui.setuser.SetUserType
import ru.cscenter.fingerpaint.ui.title.toMainActivity

class ChooseUserFragment : Fragment(), SetUserListener {

    private val usersList = MainApplication.dbController.getAllNames().toMutableList()
    private lateinit var adapter: ArrayAdapter<User>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_choose_user, container, false)
        val listView: ListView = root.findViewById(R.id.users_list)

        val navController = findNavController(this)

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, index, _ ->
            MainApplication.dbController.setCurrentUser(usersList[index].id)
            if (MainApplication.isLoading) {
                toMainActivity(activity!!)
            } else {
                navController.popBackStack()
            }
        }

        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, i: Int, _ ->
            navController.navigate(
                R.id.nav_set_user,
                SetUserFragmentArgs(SetUserType.UPDATE_USER, usersList[i].id).toBundle()
            )
            true
        }

        adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, usersList)
        listView.adapter = adapter

        val addUserButton: Button = root.findViewById(R.id.add_user_button)
        addUserButton.setOnClickListener {
            navController.navigate(
                R.id.nav_set_user,
                SetUserFragmentArgs(SetUserType.INSERT_USER).toBundle()
            )
        }

        return root
    }

    private fun saveFragment(listener: SetUserListener?) {
        val container = activity as ActivitySetUserListenerContainer?
        container?.setSetUserListener(listener)
    }

    override fun onSetUser(type: SetUserType, userId: Int) {
        val user = MainApplication.dbController.getUser(userId)
        user?.let {
            when (type) {
                SetUserType.INSERT_USER -> {
                    usersList.add(it)
                }
                SetUserType.UPDATE_USER -> {
                    val i = usersList.indexOfFirst { u -> u.id == userId }
                    usersList[i] = it
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDetach() {
        super.onDetach()
        saveFragment(null)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        saveFragment(this)
    }
}
