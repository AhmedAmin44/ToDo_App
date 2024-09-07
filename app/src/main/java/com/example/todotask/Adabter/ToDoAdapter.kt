package com.codingstuff.todolist.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.codingstuff.todolist.AddNewTask
import com.codingstuff.todolist.model.ToDoModel
import com.example.todotask.R
import com.google.firebase.firestore.FirebaseFirestore

class ToDoAdapter(
    private val context: Context,
    private val activity: FragmentActivity,
    private var todoList: MutableList<ToDoModel> // Use MutableList directly
) : RecyclerView.Adapter<ToDoAdapter.MyViewHolder>() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.each_task, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val toDoModel = todoList[position]
        holder.mCheckBox.text = toDoModel.task
        holder.mDueDateTv.text = "Due On ${toDoModel.due}"
        holder.mCheckBox.isChecked = toBoolean(toDoModel.status)
        holder.mCheckBox.setOnCheckedChangeListener { _, isChecked ->
            toDoModel.taskId?.let {
                firestore.collection("task").document(it).update("status", if (isChecked) 1 else 0)
            }
        }
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    fun deleteTask(position: Int) {
        val toDoModel = todoList[position]
        toDoModel.taskId?.let {
            firestore.collection("task").document(it).delete()
        }
        todoList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun editTask(position: Int) {
        val toDoModel = todoList[position]

        val bundle = Bundle().apply {
            putString("task", toDoModel.task)
            putString("due", toDoModel.due)
            putString("id", toDoModel.taskId)
        }

        val addNewTask = AddNewTask().apply {
            arguments = bundle
        }
        addNewTask.show(activity.supportFragmentManager, AddNewTask.TAG)
    }

    private fun toBoolean(status: Int): Boolean {
        return status != 0
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mDueDateTv: TextView = itemView.findViewById(R.id.due_date_tv)
        val mCheckBox: CheckBox = itemView.findViewById(R.id.mcheckbox)
    }
}
