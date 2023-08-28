package com.todolist.tasks.utils

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {
    @Insert
    suspend fun insert(note: Task)

    @Delete
    suspend fun delete(note: Task)

    @Update
    suspend fun update(note: Task)

    @Query("DELETE FROM tasks_table")
    suspend fun deleteAllNotes()

    @Query("SELECT * FROM tasks_table ORDER BY priority ASC")
    fun getAllTasks(): LiveData<MutableList<Task>>
}