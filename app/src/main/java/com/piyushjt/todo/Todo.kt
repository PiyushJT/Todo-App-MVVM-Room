package com.piyushjt.todo

data class Todo(
    val title : String,
    val description : String? = null,
    val isChecked : Boolean
)