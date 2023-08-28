package com.todolist.tasks.utils

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.todolist.tasks.R

class TaskAddedReceiver : BroadcastReceiver() {
    val TASK_ADDED = "com.todolist.tasks.TASK_ADDED"
    override fun onReceive(context: Context, intent: Intent) {
        // Trigger a widget update
        if (TASK_ADDED == intent.action) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val widgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(
                    context,
                    TasksWidgetService::class.java
                )
            )
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetIds, R.id.widget_stack_view)
            Toast.makeText(context, "task added!!!", Toast.LENGTH_SHORT).show()
        }
    }
}