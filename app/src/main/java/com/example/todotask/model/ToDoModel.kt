package com.codingstuff.todolist.model

class ToDoModel : TaskId() {
    var task: String? = null
    var due: String? = null
    var status: Int = 0
}
