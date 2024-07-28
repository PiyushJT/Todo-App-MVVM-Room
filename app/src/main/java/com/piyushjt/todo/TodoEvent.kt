package com.piyushjt.todo

sealed interface TodoEvent {
    data class SetTitle(val title: String) : TodoEvent
    data class SetID(val id: Int) : TodoEvent
    data class SetDescription(val description: String) : TodoEvent
    data class SetChecked(val todo: Todo, val isChecked: Boolean) : TodoEvent
    object SaveTodo : TodoEvent
    object HideAddTodo : TodoEvent
    object ShowAddTodo: TodoEvent
    data class SortTodo(val sortType: SortType = SortType.CHECKED): TodoEvent
    data class DeleteTodo(val todo: Todo): TodoEvent
}