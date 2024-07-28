package com.piyushjt.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Todo View Model
@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModel(
    // dao
    private val dao: TodoDao

) : ViewModel() {

    // Sort Type
    private val _sortType = MutableStateFlow(SortType.CHECKED)

    private val _todos = _sortType
        .flatMapLatest { sortType ->
            when (sortType) {
                SortType.CHECKED -> dao.getAllTodo()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())


    // Todo State
    private val _state = MutableStateFlow(TodoState())

    val state = combine(_state, _sortType, _todos) { state, sortType, todos ->
        state.copy(
            todos = todos,
            sortType = sortType,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TodoState())


    // On Event Functions
    fun onEvent(event: TodoEvent) {
        when (event) {

            // Delete a Task
            is TodoEvent.DeleteTodo -> {
                viewModelScope.launch {
                    dao.deleteToto(event.todo)
                }
            }

            // Save or Update a Task
            is TodoEvent.SaveTodo -> {
                val title = state.value.title
                val description = state.value.description
                val isChecked = state.value.isChecked
                val id = state.value.id

                if (title.isBlank()) {
                    return
                }

                val updateTodo = Todo(
                    id = id,
                    title = title,
                    description = description,
                    isChecked = isChecked
                )

                val newTodo = Todo(
                    title = title,
                    description = description,
                    isChecked = isChecked
                )

                if (updateTodo.id != -1) {
                    viewModelScope.launch {
                        dao.upsertTodo(updateTodo)
                    }
                }
                else {
                    viewModelScope.launch {
                        dao.upsertTodo(newTodo)
                    }
                }

                _state.update {
                    it.copy(
                        id = -1,
                        isAddingTodo = false,
                        title = "",
                        description = "",
                        isChecked = false
                    )
                }
            }

            // Setting the Title
            is TodoEvent.SetTitle -> {
                _state.update {
                    it.copy(
                        title = event.title
                    )
                }
            }

            // Setting the Id
            is TodoEvent.SetID -> {
                _state.update {
                    it.copy(
                        id = event.id
                    )
                }
            }

            // Setting the Description
            is TodoEvent.SetDescription -> {
                _state.update {
                    it.copy(
                        description = event.description
                    )
                }
            }

            // Updating Checked state
            is TodoEvent.SetChecked -> {

                viewModelScope.launch {
                    dao.updateTodo(event.todo.copy(
                        isChecked = event.isChecked
                    ))
                }
            }

            // Sort the list
            is TodoEvent.SortTodo -> {
                _sortType.value = event.sortType
            }

            // Updating isAddingTodo
            is TodoEvent.ShowAddTodo -> {
                _state.update {
                    it.copy(
                        isAddingTodo = true
                    )
                }
            }
            is TodoEvent.HideAddTodo -> {
                _state.update {
                    it.copy(
                        isAddingTodo = false
                    )
                }
            }

        }
    }
}