package com.codingstuff.todolist

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import com.example.todotask.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.HashMap

class AddNewTask : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "AddNewTask"

        @JvmStatic
        fun newInstance(): AddNewTask {
            return AddNewTask()
        }
    }

    private lateinit var setDueDate: TextView
    private lateinit var mTaskEdit: EditText
    private lateinit var mSaveBtn: Button
    private lateinit var firestore: FirebaseFirestore
    private var dueDate: String = ""
    private var id: String = ""
    private var dueDateUpdate: String = ""
    private var isUpdate = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_new_task, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDueDate = view.findViewById(R.id.setDueDate)
        mTaskEdit = view.findViewById(R.id.editText)
        mSaveBtn = view.findViewById(R.id.save_btn)

        firestore = FirebaseFirestore.getInstance()

        // Check if this is an update
        arguments?.let {
            isUpdate = true
            val task = it.getString("task")
            id = it.getString("id") ?: ""
            dueDateUpdate = it.getString("due") ?: ""

            mTaskEdit.setText(task)
            setDueDate.text = dueDateUpdate

            if (!task.isNullOrEmpty()) {
                mSaveBtn.isEnabled = false
                mSaveBtn.setBackgroundColor(Color.GRAY)
            }
        }

        // Enable/Disable Save Button based on task input
        mTaskEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    mSaveBtn.isEnabled = false
                    mSaveBtn.setBackgroundColor(Color.GRAY)
                } else {
                    mSaveBtn.isEnabled = true
                    mSaveBtn.setBackgroundColor(resources.getColor(R.color.dark_blue, null))
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Set Due Date using DatePickerDialog
        setDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            context?.let { ctx ->
                val datePickerDialog = DatePickerDialog(ctx, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                    val selectedMonth = selectedMonth + 1
                    setDueDate.text = "$selectedDay/$selectedMonth/$selectedYear"
                    dueDate = "$selectedDay/$selectedMonth/$selectedYear"
                }, year, month, day)

                datePickerDialog.show()
            } ?: run {
                Toast.makeText(context, "Error showing DatePickerDialog", Toast.LENGTH_SHORT).show()
            }
        }

        // Save Task to Firestore
        mSaveBtn.setOnClickListener {
            val task = mTaskEdit.text.toString()

            if (task.isEmpty()) {
                Toast.makeText(context, "Empty task not Allowed !!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dueDate.isEmpty()) {
                Toast.makeText(context, "Please set a due date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isUpdate) {
                firestore.collection("task").document(id)
                    .update("task", task, "due", dueDate)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Task Updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                val taskMap = HashMap<String, Any>().apply {
                    put("task", task)
                    put("due", dueDate)
                    put("status", 0)
                    put("time", FieldValue.serverTimestamp())
                }

                firestore.collection("task")
                    .add(taskMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Task Saved", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to save task: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }

            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val activity = activity
        if (activity is OnDialogCloseListener) {
            activity.onDialogClose(dialog)
        }
    }
}
