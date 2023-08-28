package com.todolist.tasks.utils

import androidx.lifecycle.LiveData

class TriggerableLiveData<T> : LiveData<T>() {
    fun triggerUpdate(data: T) {
        value = data
    }
}