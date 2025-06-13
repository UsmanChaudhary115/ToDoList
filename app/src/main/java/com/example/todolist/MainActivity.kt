package com.example.todolist

import android.app.DatePickerDialog
import android.content.Intent
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
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var textViewEmpty: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var viewModel: TaskViewModel
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupClickListeners()

        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]

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
                TaskAdapter.TaskAction.OPEN_DETAILS -> {
                    val intent = Intent(this, TaskDetailActivity::class.java).apply {
                        putExtra("title", task.title)
                        putExtra("description", task.description)
                        putExtra("setupTime", task.setupTime)
                        putExtra("deadline", task.deadline)
                    }
                    startActivity(intent)
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
        val dialogView = layoutInflater.inflate(R.layout.dialog_task_form, null)
        val editTitle = dialogView.findViewById<TextInputEditText>(R.id.editTextTitle)
        val editDescription = dialogView.findViewById<TextInputEditText>(R.id.editTextDescription)
        val deadlineView = dialogView.findViewById<TextView>(R.id.textViewDeadline)

        val calendar = Calendar.getInstance()
        var selectedDeadline: Long = calendar.timeInMillis

        deadlineView.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    selectedDeadline = calendar.timeInMillis
                    deadlineView.text = "Deadline: ${dateFormat.format(calendar.time)}"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        AlertDialog.Builder(this)
            .setTitle("Add New Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = editTitle.text.toString().trim()
                val description = editDescription.text.toString().trim()
                if (title.isNotEmpty()) {
                    val task = Task(
                        title = title,
                        description = description,
                        setupTime = System.currentTimeMillis(),
                        deadline = selectedDeadline
                    )
                    viewModel.insert(task)
                    Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Enter task title", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditTaskDialog(task: Task) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_task_form, null)
        val editTitle = dialogView.findViewById<TextInputEditText>(R.id.editTextTitle)
        val editDescription = dialogView.findViewById<TextInputEditText>(R.id.editTextDescription)
        val deadlineView = dialogView.findViewById<TextView>(R.id.textViewDeadline)

        editTitle.setText(task.title)
        editDescription.setText(task.description)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = task.deadline
        }
        var selectedDeadline = task.deadline
        deadlineView.text = "Deadline: ${dateFormat.format(Date(task.deadline))}"

        deadlineView.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    selectedDeadline = calendar.timeInMillis
                    deadlineView.text = "Deadline: ${dateFormat.format(calendar.time)}"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        AlertDialog.Builder(this)
            .setTitle("Edit Task")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val newTitle = editTitle.text.toString().trim()
                val newDescription = editDescription.text.toString().trim()
                if (newTitle.isNotEmpty()) {
                    val updated = task.copy(
                        title = newTitle,
                        description = newDescription,
                        deadline = selectedDeadline
                    )
                    viewModel.update(updated)
                    Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}