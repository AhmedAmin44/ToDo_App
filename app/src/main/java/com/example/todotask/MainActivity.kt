package com.codingstuff.todolist

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codingstuff.todolist.adapter.ToDoAdapter
import com.codingstuff.todolist.model.ToDoModel
import com.example.todotask.TouchHelper
import com.example.todotask.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity(), OnDialogCloseListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mFab: FloatingActionButton
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: ToDoAdapter
    private val mList = mutableListOf<ToDoModel>()
    private lateinit var query: Query
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        mFab = findViewById(R.id.floatingActionButton4)
        firestore = FirebaseFirestore.getInstance()

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        mFab.setOnClickListener {
            AddNewTask.newInstance().show(supportFragmentManager, AddNewTask.TAG)
        }

        adapter = ToDoAdapter(this, this, mList)

        val itemTouchHelper = ItemTouchHelper(TouchHelper(this, adapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)

        recyclerView.adapter = adapter

        showData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showData() {
        query = firestore.collection("task").orderBy("time", Query.Direction.DESCENDING)

        listenerRegistration = query.addSnapshotListener { value, error ->
            if (error != null) {
                // Handle error
                return@addSnapshotListener
            }

            value?.documentChanges?.forEach { documentChange ->
                when (documentChange.type) {
                    DocumentChange.Type.ADDED -> {
                        val id = documentChange.document.id
                        val toDoModel = documentChange.document.toObject(ToDoModel::class.java)
                            .withId<ToDoModel>(id)
                        mList.add(toDoModel)
                        adapter.notifyDataSetChanged()
                    }
                    // Handle other document change types if needed
                    DocumentChange.Type.MODIFIED -> TODO()
                    DocumentChange.Type.REMOVED -> TODO()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDialogClose(dialogInterface: DialogInterface) {
        mList.clear()
        showData()
        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove() // Stop listening when activity is destroyed
    }
}
