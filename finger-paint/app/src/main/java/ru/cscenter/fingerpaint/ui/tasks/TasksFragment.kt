package ru.cscenter.fingerpaint.ui.tasks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.api.ApiChooseTask
import ru.cscenter.fingerpaint.ui.games.ExtraChooseActivity

class TasksFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_tasks, container, false)
        val listView: RecyclerView = root.findViewById(R.id.tasks_list)
        val startButton: Button = root.findViewById(R.id.start_button)
        startButton.visibility = View.INVISIBLE
        MainApplication.synchronizeController.loadChooseTasks({ list ->
            val adapter = TasksAdapter(context!!, list)
            listView.adapter = adapter
            adapter.getChosenSize().observe(viewLifecycleOwner, Observer { size ->
                startButton.visibility = if (size > 0) View.VISIBLE else View.INVISIBLE
            })
            startButton.setOnClickListener { startExtraGame(adapter.getChosen()) }
        }, {
            Snackbar.make(root, getString(R.string.tasks_request_failed), Snackbar.LENGTH_LONG).show()
        })

        return root
    }

    private fun startExtraGame(tasks: List<ApiChooseTask>) {
        val intent = Intent(activity, ExtraChooseActivity::class.java)
        intent.putExtra(ExtraChooseActivity.ARG_CHOOSE_TASK, tasks.toTypedArray())
        startActivity(intent)
    }
}
