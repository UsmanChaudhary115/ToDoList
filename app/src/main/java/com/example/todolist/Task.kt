package com.example.todolist

data class Task(
    val id: Long,
    val title: String,
    var isCompleted: Boolean = false
)