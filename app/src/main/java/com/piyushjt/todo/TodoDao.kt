package com.piyushjt.todo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Upsert
    suspend fun upsertTodo(todo: Todo)

    @Update
    suspend fun updateTodo(todo: Todo)

    @Delete
    suspend fun deleteToto(todo: Todo)

    @Query("SELECT * FROM todo ORDER BY isChecked ASC")
    fun getAllTodo() : Flow<List<Todo>>

}