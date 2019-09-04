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
import com.todoapp.todo.db.Todo
import com.todoapp.todo.db.Database

class MainActivity : AppCompatActivity() {

    private val MAIN = "MainActivity"
    private lateinit var helper: Database
    private lateinit var todoListView: ListView
    private var adapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todoListView = findViewById(R.id.list_todo)

        helper = Database(this)
        updatePage()
    }

    fun doneTodo(view: View) {
        val parent = view.getParent() as View
        val todoTextView = parent.findViewById<TextView>(R.id.todo_title)
        val task = todoTextView.text.toString()
        val db = helper.writableDatabase
        db.delete(Todo.TodoEntry.TABLE,
            Todo.TodoEntry.COL_TODO_TITLE + " = ?",
            arrayOf(task))
        db.close()
        updatePage()
    }

    private fun updatePage() {
        val todoList = ArrayList<String>()
        val db = helper.readableDatabase
        val cursor = db.query(
            Todo.TodoEntry.TABLE,
            arrayOf(Todo.TodoEntry._ID, Todo.TodoEntry.COL_TODO_TITLE), null, null, null, null, null)
        while (cursor.moveToNext()) {
            val idx = cursor.getColumnIndex(Todo.TodoEntry.COL_TODO_TITLE)
            todoList.add(cursor.getString(idx))
        }

        if (adapter == null) {
            adapter = ArrayAdapter(this,
                R.layout.todo_list_item,
                R.id.todo_title,
                todoList)
            todoListView.adapter = adapter
        } else {
            adapter?.clear()
            adapter?.addAll(todoList)
            adapter?.notifyDataSetChanged()
        }

        cursor.close()
        db.close()
    }

    //ALERT DIALOG
    fun alertDialog(view: View) {
        val todoEditText = EditText(this)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Add a new todo")
            .setView(todoEditText)
            .setPositiveButton("Add", DialogInterface.OnClickListener { dialog, which ->
                val todo = todoEditText.text.toString()
                val db = helper.getWritableDatabase()
                val values = ContentValues()
                values.put(Todo.TodoEntry.COL_TODO_TITLE, todo)
                db.insertWithOnConflict(Todo.TodoEntry.TABLE,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE)
                db.close()

                Log.d(MAIN, "Todo to add: " + todo)
                updatePage()
            })
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }
}
