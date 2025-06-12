package com.example.todolist

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    private lateinit var textViewEmpty: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var fabAdd: FloatingActionButton
    private val taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupClickListeners()
        updateEmptyState()

        // Add some sample tasks
//        addSampleTasks()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewTasks)
        fabAdd = findViewById(R.id.fabAdd)
        textViewEmpty = findViewById(R.id.textViewEmpty)
    }
    private fun updateEmptyState() {
        if (taskList.isEmpty()) {
            textViewEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            textViewEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(taskList) { task, action ->
            when (action) {
                TaskAdapter.TaskAction.TOGGLE_COMPLETE -> {
                    task.isCompleted = !task.isCompleted
                    taskAdapter.notifyDataSetChanged()
                    val status = if (task.isCompleted) "completed" else "incomplete"
                    Toast.makeText(this, "Task marked as $status", Toast.LENGTH_SHORT).show()
                }
                TaskAdapter.TaskAction.DELETE -> {
                    deleteTask(task)
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
                    addTask(taskTitle)
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

        // Pre-fill with existing task title
        editTextTask.setText(task.title)
        editTextTask.setSelection(task.title.length) // Move cursor to end

        AlertDialog.Builder(this)
            .setTitle("Edit Task")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val newTitle = editTextTask.text.toString().trim()
                if (newTitle.isNotEmpty() && newTitle != task.title) {
                    updateTask(task, newTitle)
                } else if (newTitle.isEmpty()) {
                    Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateTask(task: Task, newTitle: String) {
        val position = taskList.indexOf(task)
        if (position != -1) {
            // Create updated task (since title is val, we need a new instance)
            val updatedTask = Task(task.id, newTitle, task.isCompleted)
            taskList[position] = updatedTask
            taskAdapter.notifyItemChanged(position)
            Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addTask(title: String) {
        val newTask = Task(
            id = System.currentTimeMillis(),
            title = title,
            isCompleted = false
        )
        taskList.add(0, newTask) // Add to top of list
        taskAdapter.notifyItemInserted(0)
        recyclerView.scrollToPosition(0)
        updateEmptyState()
        Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
    }

    private fun deleteTask(task: Task) {
        val position = taskList.indexOf(task)
        if (position != -1) {
            taskList.removeAt(position)
            taskAdapter.notifyItemRemoved(position)
            updateEmptyState()
            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addSampleTasks() {
        val sampleTasks = listOf(
            Task(1, "Buy groceries", false),
            Task(2, "Complete project report", true),
            Task(3, "Call dentist for appointment", false),
            Task(4, "Exercise for 30 minutes", false)
        )
        taskList.addAll(sampleTasks)
        taskAdapter.notifyDataSetChanged()
    }
}