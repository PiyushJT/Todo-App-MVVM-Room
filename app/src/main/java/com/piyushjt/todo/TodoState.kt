package com.piyushjt.todo

data class TodoState(
    val todos: List<Todo> = emptyList(),
    val id : Int = -1,
    val title: String = "",
    val description: String = "",
    val isChecked: Boolean = false,
    val isAddingTodo: Boolean = false,
    val sortType: SortType = SortType.CHECKED
)
