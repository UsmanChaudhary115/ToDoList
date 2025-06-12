package com.example.todolist

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    private lateinit var textViewEmpty: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupClickListeners()

        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        // Observe changes in the task list
        viewModel.allTasks.observe(this) { tasks ->
            taskAdapter.updateTasks(tasks.toMutableList())
            updateEmptyState(tasks.isEmpty())
        }
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewTasks)
        fabAdd = findViewById(R.id.fabAdd)
        textViewEmpty = findViewById(R.id.textViewEmpty)
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        textViewEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(mutableListOf()) { task, action ->
            when (action) {
                TaskAdapter.TaskAction.TOGGLE_COMPLETE -> {
                    viewModel.update(task.copy(isCompleted = !task.isCompleted))
                }
                TaskAdapter.TaskAction.DELETE -> {
                    viewModel.delete(task)
                }
                TaskAdapter.TaskAction.EDIT -> {
                    showEditTaskDialog(task)
                }
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter
    }

    private fun setupClickListeners() {
        fabAdd.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun showAddTaskDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
        val editTextTask = dialogView.findViewById<TextInputEditText>(R.id.editTextTask)

        AlertDialog.Builder(this)
            .setTitle("Add New Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val taskTitle = editTextTask.text.toString().trim()
                if (taskTitle.isNotEmpty()) {
                    val newTask = Task(id = 0, title = taskTitle) // Let Room auto-generate ID
                    viewModel.insert(newTask)
                    Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditTaskDialog(task: Task) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
        val editTextTask = dialogView.findViewById<TextInputEditText>(R.id.editTextTask)
        editTextTask.setText(task.title)
        editTextTask.setSelection(task.title.length)

        AlertDialog.Builder(this)
            .setTitle("Edit Task")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val newTitle = editTextTask.text.toString().trim()
                if (newTitle.isNotEmpty()) {
                    viewModel.update(task.copy(title = newTitle))
                    Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
