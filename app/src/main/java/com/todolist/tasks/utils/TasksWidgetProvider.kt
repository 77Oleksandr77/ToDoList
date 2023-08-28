package com.todolist.tasks.utils

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import com.todolist.tasks.R
import com.todolist.tasks.activities.MainActivity
import com.todolist.tasks.activities.TasksWidgetConfig

class TasksWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        appWidgetIds?.forEach {
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            val prefs =
                context?.getSharedPreferences(TasksWidgetConfig.SHARED_PREFS, Context.MODE_PRIVATE)
            val buttonText = prefs?.getString(TasksWidgetConfig.KEY_BUTTON_TEXT + it, "Task name")

            val serviceIntent = Intent(context,TasksWidgetService::class.java)
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,it)
            serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))

            val views = RemoteViews(context?.packageName, R.layout.tasks_widget)
            views.setOnClickPendingIntent(R.id.widget_button, pendingIntent)
            views.setCharSequence(R.id.widget_button, "setText", buttonText)
            views.setRemoteAdapter(R.id.widget_stack_view, serviceIntent)

            val widgetOptions = appWidgetManager?.getAppWidgetOptions(it)
            views.setEmptyView(R.id.widget_stack_view,R.id.widget_empty_view)
            resizeWidget(widgetOptions!!,views)
            appWidgetManager.updateAppWidget(it, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(it,R.id.widget_stack_view)

        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        val views = RemoteViews(context?.packageName, R.layout.tasks_widget)

        resizeWidget(newOptions!!,views)
        appWidgetManager?.updateAppWidget(appWidgetId, views)
    }


    private fun resizeWidget(widgetOptions: Bundle, views: RemoteViews) {
        val maxHeight = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)

        if (maxHeight > 100) {
            views.setViewVisibility(R.id.widget_button, View.VISIBLE)
        } else {
            views.setViewVisibility(R.id.widget_button, View.GONE)
        }
    }

    override fun onDisabled(context: Context?) {
    }

    override fun onEnabled(context: Context?) {

    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {

    }
}