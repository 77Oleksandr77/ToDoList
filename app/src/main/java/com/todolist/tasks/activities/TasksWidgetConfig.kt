package com.todolist.tasks.activities

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RemoteViews
import com.todolist.tasks.R
import com.todolist.tasks.utils.TasksWidgetService

class TasksWidgetConfig : AppCompatActivity() {
    companion object {
        const val SHARED_PREFS = "com.todolist.tasks.prefs"
        const val KEY_BUTTON_TEXT = "com.todolist.tasks.keyButtonText"

    }

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var editTextButton: EditText
    private lateinit var buttonConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_widget_config)
        editTextButton = findViewById(R.id.edit_text_button)
        buttonConfirm = findViewById(R.id.confirm_configuration_button)

        buttonConfirm.setOnClickListener {
            confirmConfiguration()
        }

        val configIntent = intent
        val extras = configIntent.extras

        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId)
        setResult(RESULT_CANCELED,resultValue)

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }

    }

    private fun confirmConfiguration() {
        val appWidgetManager = AppWidgetManager.getInstance(this)

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val buttonText = editTextButton.text.toString()

        val serviceIntent = Intent(this,TasksWidgetService::class.java)
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId)
        serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))

        val views = RemoteViews(this.packageName, R.layout.tasks_widget)
        views.setOnClickPendingIntent(R.id.widget_button, pendingIntent)
        views.setCharSequence(R.id.widget_button, "setText", buttonText)
        views.setRemoteAdapter(R.id.widget_stack_view, serviceIntent)
        views.setEmptyView(R.id.widget_stack_view, R.id.widget_empty_view)
        appWidgetManager.updateAppWidget(appWidgetId,views)

        val prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(KEY_BUTTON_TEXT + appWidgetId,buttonText)
        editor.apply()

        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId)
        setResult(RESULT_OK,resultValue)
        finish()
    }
}