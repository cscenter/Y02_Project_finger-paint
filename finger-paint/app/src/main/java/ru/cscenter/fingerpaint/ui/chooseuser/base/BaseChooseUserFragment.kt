package ru.cscenter.fingerpaint.ui.chooseuser.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.models.CurrentUserModel
import ru.cscenter.fingerpaint.models.UsersModel

abstract class BaseChooseUserFragment : Fragment() {

    abstract fun getAdapter(
        activity: Activity,
        navController: NavController
    ): ChooseUserAdapter<out BaseUserViewHolder>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val usersModel: UsersModel by activityViewModels()
        val root = inflater.inflate(R.layout.fragment_choose_user, container, false)
        val navController = findNavController()
        val adapter = getAdapter(activity!!, navController)
        val listView: RecyclerView = root.findViewById(R.id.users_list)
        listView.layoutManager = LinearLayoutManager(activity)
        listView.adapter = adapter
        val addNewUserMessage: TextView = root.findViewById(R.id.add_new_user_message)

        usersModel.users.observe(viewLifecycleOwner, Observer { users ->
            adapter.users = users
            addNewUserMessage.visibility = if (users.isEmpty()) View.VISIBLE else View.INVISIBLE
        })

        val addUserButton: Button = root.findViewById(R.id.add_user_button)
        addUserButton.setOnClickListener { navController.navigate(R.id.nav_add_user) }

        return root
    }

    protected val onChooseUser: (User) -> Unit = { user: User ->
        GlobalScope.launch(Dispatchers.Main) {
            val currentUserModel: CurrentUserModel by activityViewModels()
            currentUserModel.setCurrentUser(user)
        }
    }
}
