package com.codingstuff.todolist.model

import androidx.annotation.NonNull
import com.google.firebase.firestore.Exclude

open class TaskId {
    @Exclude
    var taskId: String? = null

    fun <T : TaskId> withId(@NonNull id: String): T {
        this.taskId = id
        return this as T
    }
}
