package ru.cscenter.fingerpaint.ui.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.api.ApiChooseTask
import ru.cscenter.fingerpaint.network.FingerPaintApi

class TasksAdapter(context: Context, private val data: List<ApiChooseTask>) :
    RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    class TaskViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val question = view.findViewById<TextView>(R.id.question)
        private val image1 = view.findViewById<ImageView>(R.id.first_choose)
        private val image2 = view.findViewById<ImageView>(R.id.second_choose)

        fun bind(task: ApiChooseTask) {
            question.text = task.text
            Glide.with(view).load(FingerPaintApi.pictureUrl(task.correctImageId)).into(image1)
            Glide.with(view).load(FingerPaintApi.pictureUrl(task.incorrectImageId)).into(image2)
        }
    }

    private val layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = layoutInflater.inflate(R.layout.choose_task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = data[position]
        holder.bind(task)
    }

}
