package com.example.todolist

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskAction: (Task, TaskAction) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    enum class TaskAction {
        TOGGLE_COMPLETE,
        DELETE,
        EDIT,
        OPEN_DETAILS
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBoxComplete: CheckBox = itemView.findViewById(R.id.checkBoxComplete)
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.textViewTitle.text = task.title
        holder.checkBoxComplete.isChecked = task.isCompleted
        updateTaskAppearance(holder, task.isCompleted)

        // Completion toggle
        holder.checkBoxComplete.setOnClickListener {
            onTaskAction(task, TaskAction.TOGGLE_COMPLETE)
        }

        // Delete button
        holder.buttonDelete.setOnClickListener {
            onTaskAction(task, TaskAction.DELETE)
        }

        // Tap → open details
        holder.itemView.setOnClickListener {
            onTaskAction(task, TaskAction.OPEN_DETAILS)
        }

        // Long press → edit
        holder.itemView.setOnLongClickListener {
            onTaskAction(task, TaskAction.EDIT)
            true
        }
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: MutableList<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    private fun updateTaskAppearance(holder: TaskViewHolder, isCompleted: Boolean) {
        if (isCompleted) {
            holder.textViewTitle.paintFlags =
                holder.textViewTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.textViewTitle.setTextColor(
                ContextCompat.getColor(holder.itemView.context, android.R.color.darker_gray)
            )
        } else {
            holder.textViewTitle.paintFlags =
                holder.textViewTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.textViewTitle.setTextColor(
                ContextCompat.getColor(holder.itemView.context, android.R.color.black)
            )
        }
    }
}