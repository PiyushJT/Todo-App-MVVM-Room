package com.piyushjt.todo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Todo(
    val title : String,
    val description : String?,
    val isChecked : Boolean,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)