package com.todolist.tasks.utils

import androidx.lifecycle.LiveData

class TaskRepository(private val notesDao: TaskDao) {
    val allNotes: LiveData<MutableList<Task>> = notesDao.getAllTasks()

    suspend fun insert(note: Task) {
        notesDao.insert(note)
    }

    suspend fun delete(note: Task) {
        notesDao.delete(note)
    }

    suspend fun update(note: Task) {
        notesDao.update(note)
    }

    suspend fun deleteAllTasks() {
        notesDao.deleteAllNotes()
    }
}