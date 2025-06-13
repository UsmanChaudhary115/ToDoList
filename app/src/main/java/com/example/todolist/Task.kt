package com.example.todolist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val setupTime: Long = System.currentTimeMillis(),
    val deadline: Long = 0L,
    val isCompleted: Boolean = false
)
