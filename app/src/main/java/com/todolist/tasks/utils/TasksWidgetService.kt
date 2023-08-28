package com.todolist.tasks.utils

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.todolist.tasks.R
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

class TasksWidgetService() : RemoteViewsService() {
    lateinit var alltasksLiveData: LiveData<MutableList<Task>>
    lateinit var viewModel: TaskViewModel
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(TaskViewModel::class.java)
        alltasksLiveData = viewModel.allTasks
        return TasksWidgetItemFactory(applicationContext, intent!!)
    }

    inner class TasksWidgetItemFactory(context: Context, intent: Intent) : RemoteViewsFactory {
        private var context: Context
        private var appWidgetId = 0
        private var tasksData: MutableList<Task> = mutableListOf()
        init {
            this.context = context
            this.appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            alltasksLiveData.observeForever { tasks ->
                tasksData.clear()
                tasksData.addAll(tasks)
                AppWidgetManager.getInstance(context)
                    .notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_stack_view)
            }

        }

        override fun onCreate() {
            //connect to data source
        }

        override fun onDataSetChanged() {

        }


        override fun onDestroy() {
            // close the connection
        }

        override fun getCount(): Int {
            return tasksData.size
        }

        override fun getViewAt(postion: Int): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_item)
            views.setTextViewText(R.id.widget_task_name, tasksData[postion].title)
            views.setTextViewText(R.id.widget_task_date, tasksData[postion].description)
            return views
        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

    }

}