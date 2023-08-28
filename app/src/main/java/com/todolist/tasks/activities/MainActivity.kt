package com.todolist.tasks.activities

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.todolist.tasks.R
import com.todolist.tasks.adaptors.TasksAdaptor
import com.todolist.tasks.utils.Constants
import com.todolist.tasks.utils.Task
import com.todolist.tasks.utils.TaskAddedReceiver
import com.todolist.tasks.utils.TaskViewModel

class MainActivity : AppCompatActivity(), TasksAdaptor.OnClickListener {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var notesAdaptor: TasksAdaptor
    private lateinit var addNoteButton: FloatingActionButton
    private lateinit var getResult: ActivityResultLauncher<Intent>
    private val taskAddedReceiver = TaskAddedReceiver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addNoteButton = findViewById(R.id.add_note_button)

        recyclerView = findViewById(R.id.recycler_view)
        notesAdaptor = TasksAdaptor(this)
        recyclerView.adapter = notesAdaptor
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        taskViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(application)
        )[TaskViewModel::class.java]

        taskViewModel.allTasks.observe(this) {
            /*
            here we add data to our recycler view
             */
            notesAdaptor.submitList(it)
        }

        getResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Constants.REQUEST_CODE) {
                    val title = it.data?.getStringExtra(Constants.EXTRA_TITLE)
                    val description = it.data?.getStringExtra(Constants.EXTRA_DESCRIPTION)
                    val priority = it.data?.getIntExtra(Constants.EXTRA_PRIORITY, 0)

                    val note = Task(title!!, description!!, priority!!)
                    taskViewModel.addNote(note)


                    val taskAddedIntent = Intent("com.todolist.tasks.TASK_ADDED")
                    sendBroadcast(taskAddedIntent)
                } else if (it.resultCode == Constants.EDIT_REQUEST_CODE) {
                    val title = it.data?.getStringExtra(Constants.EXTRA_TITLE)
                    val description = it.data?.getStringExtra(Constants.EXTRA_DESCRIPTION)
                    val priority = it.data?.getIntExtra(Constants.EXTRA_PRIORITY, 0)
                    val id = it.data?.getIntExtra(Constants.EXTRA_ID, 0)
                    val note = Task(title!!, description!!, priority!!)
                    note.id = id!!

                    taskViewModel.updateNote(note)

                    val taskAddedIntent = Intent("com.todolist.tasks.TASK_ADDED")
                    sendBroadcast(taskAddedIntent)
                }
            }
        addNoteButton.setOnClickListener {
            val intent = Intent(this@MainActivity, AddEditActivity::class.java)
            getResult.launch(intent)
        }

        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val removedNote = notesAdaptor.getNoteAt(viewHolder.adapterPosition)
                taskViewModel.deleteNote(notesAdaptor.getNoteAt(viewHolder.adapterPosition))

                Snackbar.make(this@MainActivity, recyclerView, "Deleted note", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        taskViewModel.addNote(removedNote)
                    }.show()
            }

        }).attachToRecyclerView(recyclerView)

        val intentFilter = IntentFilter("com.todolist.tasks.TASK_ADDED")
        registerReceiver(taskAddedReceiver,intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(taskAddedReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all_notes -> {
                taskViewModel.deleteAllNote()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(note: Task) {
        val title = note.title
        val description = note.description
        val priority = note.priority
        val id = note.id

        val note = Task(title, description, priority)
        note.id = id

        val intent = Intent(this@MainActivity, AddEditActivity::class.java)
        intent.putExtra(Constants.EXTRA_TITLE, title)
        intent.putExtra(Constants.EXTRA_DESCRIPTION, description)
        intent.putExtra(Constants.EXTRA_PRIORITY, priority)
        intent.putExtra(Constants.EXTRA_ID, id)

        getResult.launch(intent)
    }

}