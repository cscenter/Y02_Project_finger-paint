package ru.cscenter.fingerpaint.ui.chooseuser.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.cscenter.fingerpaint.db.User


open class ChooseUserAdapter<T : BaseUserViewHolder>(
    context: Context,
    private val resourceId: Int,
    private val createHolder: (View) -> T
) : RecyclerView.Adapter<T>() {

    var users = emptyList<User>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T {
        val view = layoutInflater.inflate(resourceId, parent, false)
        return createHolder(view)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: T, position: Int) {
        val user = users[position]
        holder.bindUser(user)
    }
}
