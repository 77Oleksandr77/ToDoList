package com.todolist.tasks.utils

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    val allTasks: LiveData<MutableList<Task>>
    private val repository: TaskRepository

    init {
        val dao = TasksDatabase.getInstance(application).getNotesDao()
        repository = TaskRepository(dao)
        allTasks = repository.allNotes
    }

    fun addNote(note: Task) = viewModelScope.launch {
        repository.insert(note)
    }

    fun updateNote(note: Task) = viewModelScope.launch {
        repository.update(note)
    }

    fun deleteNote(note: Task) = viewModelScope.launch {
        repository.delete(note)
    }

    fun deleteAllNote() = viewModelScope.launch {
        repository.deleteAllTasks()
    }
}