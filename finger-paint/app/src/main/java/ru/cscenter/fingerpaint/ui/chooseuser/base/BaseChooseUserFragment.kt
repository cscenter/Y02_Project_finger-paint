package ru.cscenter.fingerpaint.ui.chooseuser.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.cscenter.fingerpaint.ActivitySetUserListenerContainer
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.ui.setuser.SetUserFragmentArgs
import ru.cscenter.fingerpaint.ui.setuser.SetUserListener
import ru.cscenter.fingerpaint.ui.setuser.SetUserType
import ru.cscenter.fingerpaint.ui.title.toTitleChooseUserActivity

abstract class BaseChooseUserFragment : Fragment(), SetUserListener {

    private val usersList = MainApplication.dbController.getAllNames().toMutableList()
    private lateinit var adapter: BaseChooseUserAdapter<out BaseUserViewHolder>
    private lateinit var addNewUserMessage: TextView

    abstract fun getAdapter(
        activity: Activity,
        users: MutableList<User>,
        navController: NavController
    ): BaseChooseUserAdapter<out BaseUserViewHolder>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_choose_user, container, false)
        adapter = getAdapter(activity!!, usersList, findNavController())
        val listView: RecyclerView = root.findViewById(R.id.users_list)
        listView.layoutManager = LinearLayoutManager(activity)
        listView.adapter = adapter

        addNewUserMessage = root.findViewById(R.id.add_new_user_message)
        setAddNewUserMessageVisibility()

        val navController = findNavController()
        val addUserButton: Button = root.findViewById(R.id.add_user_button)
        addUserButton.setOnClickListener {
            navController.navigate(
                R.id.nav_set_user,
                SetUserFragmentArgs(SetUserType.INSERT_USER).toBundle()
            )
        }

        return root
    }

    protected fun onDeleteUser(user: User) {
        usersList.remove(user)
        adapter.notifyDataSetChanged()
        MainApplication.dbController.deleteUser(user)
        if (!MainApplication.dbController.hasCurrentUser()) {
            toTitleChooseUserActivity(activity!!)
        }
    }

    private fun setAddNewUserMessageVisibility() {
        addNewUserMessage.visibility = if (usersList.isEmpty()) View.VISIBLE else View.INVISIBLE
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
            setAddNewUserMessageVisibility()
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
