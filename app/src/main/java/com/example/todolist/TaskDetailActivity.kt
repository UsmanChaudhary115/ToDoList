package com.example.todolist

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        // Set up toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Get task data
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description") ?: ""
        val setupTime = intent.getLongExtra("setupTime", 0L)
        val deadline = intent.getLongExtra("deadline", 0L)

        // Set toolbar title
        supportActionBar?.title = title ?: "Task Details"

        // Update UI
        findViewById<TextView>(R.id.textTitle).text = title ?: "No Title"
        findViewById<TextView>(R.id.textDescription).text = if (description.isNotEmpty()) description else "No description"
        findViewById<TextView>(R.id.textSetupTime).text = "Created: ${formatTime(setupTime)}"
        findViewById<TextView>(R.id.textDeadline).text = "Deadline: ${formatTime(deadline)}"
    }

    private fun formatTime(time: Long): String {
        return if (time > 0) {
            SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault()).format(Date(time))
        } else {
            "None"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}