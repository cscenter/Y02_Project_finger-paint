package ru.cscenter.fingerpaint.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R

class TasksFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_tasks, container, false)
        val listView: RecyclerView = root.findViewById(R.id.tasks_list)
        MainApplication.synchronizeController.loadChooseTasks({ list ->
            val adapter = TasksAdapter(context!!, list)
            listView.adapter = adapter
        }, {
            Snackbar.make(root, getString(R.string.tasks_request_failed), Snackbar.LENGTH_LONG).show()
        })

        return root
    }
}
