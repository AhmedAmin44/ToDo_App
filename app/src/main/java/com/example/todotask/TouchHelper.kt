package com.example.todotask

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.codingstuff.todolist.adapter.ToDoAdapter

class TouchHelper(
    private val activity: FragmentActivity,
    private val adapter: ToDoAdapter
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    // Retrieve the delete and edit icons safely
    private val deleteIcon: Drawable? = ContextCompat.getDrawable(activity, R.drawable.delete)
    private val editIcon: Drawable? = ContextCompat.getDrawable(activity, R.drawable.edit)
    private val greenBlueColor = ContextCompat.getColor(activity, R.color.green_blue)

    // onMove method should return false as moving items is not implemented
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    // Handle the swipe actions
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition

        when (direction) {
            ItemTouchHelper.RIGHT -> {
                // Show an AlertDialog for deletion confirmation
                val builder = AlertDialog.Builder(activity)
                builder.setMessage("Are you sure you want to delete this task?")
                    .setTitle("Delete Task")
                    .setPositiveButton("Yes") { _, _ ->
                        // Safely delete the task
                        if (position != RecyclerView.NO_POSITION) {
                            adapter.deleteTask(position)
                        }
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        // Cancel the deletion and restore the item
                        adapter.notifyItemChanged(position)
                        dialog.dismiss()
                    }

                val dialog = builder.create()
                dialog.show()
            }

            ItemTouchHelper.LEFT -> {
                // Safely trigger task editing
                if (position != RecyclerView.NO_POSITION) {
                    adapter.editTask(position)
                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView

        val paint = Paint().apply {
            color = Color.RED
        }

        if (dX > 0) { // Swiping right for delete
            // Draw background and delete icon
            c.drawRect(
                itemView.left.toFloat(),
                itemView.top.toFloat(),
                itemView.left + dX,
                itemView.bottom.toFloat(),
                paint
            )
            deleteIcon?.let {
                val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                val iconTop = itemView.top + iconMargin
                val iconBottom = iconTop + it.intrinsicHeight
                val iconLeft = itemView.left + iconMargin
                val iconRight = iconLeft + it.intrinsicWidth
                it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                it.draw(c)
            }
        } else if (dX < 0) { // Swiping left for edit
            // Draw background and edit icon
            c.drawRect(
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat(),
                paint
            )
            editIcon?.let {
                val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                val iconTop = itemView.top + iconMargin
                val iconBottom = iconTop + it.intrinsicHeight
                val iconRight = itemView.right - iconMargin
                val iconLeft = iconRight - it.intrinsicWidth
                it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                it.draw(c)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
