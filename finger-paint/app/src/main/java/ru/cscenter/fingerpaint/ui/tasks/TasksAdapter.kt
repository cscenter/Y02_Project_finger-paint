package ru.cscenter.fingerpaint.ui.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.api.ApiChooseTask
import ru.cscenter.fingerpaint.network.FingerPaintApi

class TasksAdapter(
    context: Context, private val data: List<ApiChooseTask>
) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    private val isChosen = MutableList(data.size) { false }
    private val size = MutableLiveData(0)

    fun getChosen() = data.filterIndexed { i, _ -> isChosen[i] }
    fun getChosenSize(): LiveData<Int> = size

    inner class TaskViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {
        private val question = view.findViewById<TextView>(R.id.question)
        private val image1 = view.findViewById<ImageView>(R.id.first_choose)
        private val image2 = view.findViewById<ImageView>(R.id.second_choose)
        private val card = view.findViewById<LinearLayout>(R.id.task_card)

        fun bind(position: Int) {
            val task = data[position]
            question.text = task.text
            Glide.with(view).load(FingerPaintApi.pictureUrl(task.correctImageId)).placeholder(R.drawable.ic_loading_icon).into(image1)
            Glide.with(view).load(FingerPaintApi.pictureUrl(task.incorrectImageId)).placeholder(R.drawable.ic_loading_icon).into(image2)
            updateBackGround(position)
            view.setOnClickListener {
                isChosen[position] = !isChosen[position]
                updateBackGround(position)
                size.postValue(getChosen().size)
            }
        }

        private fun updateBackGround(position: Int) {
            val resource = if (isChosen[position]) R.drawable.selected_border else R.drawable.unselected_border
            card.setBackgroundResource(resource)
        }
    }

    private val layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = layoutInflater.inflate(R.layout.choose_task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount() = data.size
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) = holder.bind(position)

}
