package com.example.todoapp

import android.content.ContentValues
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.todoapp.todo.db.TodoContract
import com.todoapp.todo.db.Database

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var mHelper: Database
    private lateinit var mTodoListView: ListView
    private var mAdapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTodoListView = findViewById(R.id.list_todo)

        mHelper = Database(this)
        updateUI()
    }

    fun doneTodo(view: View) {
        val parent = view.getParent() as View
        val todoTextView = parent.findViewById<TextView>(R.id.todo_title)
        val task = todoTextView.text.toString()
        val db = mHelper.writableDatabase
        db.delete(TodoContract.TodoEntry.TABLE,
            TodoContract.TodoEntry.COL_TODO_TITLE + " = ?",
            arrayOf(task))
        db.close()
        updateUI()
    }

    private fun updateUI() {
        val taskList = ArrayList<String>()
        val db = mHelper.readableDatabase
        val cursor = db.query(
            TodoContract.TodoEntry.TABLE,
            arrayOf(TodoContract.TodoEntry._ID, TodoContract.TodoEntry.COL_TODO_TITLE), null, null, null, null, null)
        while (cursor.moveToNext()) {
            val idx = cursor.getColumnIndex(TodoContract.TodoEntry.COL_TODO_TITLE)
            taskList.add(cursor.getString(idx))
        }

        if (mAdapter == null) {
            mAdapter = ArrayAdapter(this,
                R.layout.todo_list_item,
                R.id.todo_title,
                taskList)
            mTodoListView.adapter = mAdapter
        } else {
            mAdapter?.clear()
            mAdapter?.addAll(taskList)
            mAdapter?.notifyDataSetChanged()
        }

        cursor.close()
        db.close()
    }

    //ALERT DIALOG
    fun alertDialog(view: View) {
        val taskEditText = EditText(this)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Add a new todo")
            .setView(taskEditText)
            .setPositiveButton("Add", DialogInterface.OnClickListener { dialog, which ->
                val todo = taskEditText.text.toString()
                val db = mHelper.getWritableDatabase()
                val values = ContentValues()
                values.put(TodoContract.TodoEntry.COL_TODO_TITLE, todo)
                db.insertWithOnConflict(TodoContract.TodoEntry.TABLE,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE)
                db.close()

                Log.d(TAG, "Todo to add: " + todo)
                updateUI()
            })
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }
}
