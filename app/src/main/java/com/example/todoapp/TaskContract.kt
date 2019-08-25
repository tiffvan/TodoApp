package com.todoapp.todo.db

import android.provider.BaseColumns

class TaskContract {
    companion object {
        val DB_NAME = "com.todoapp.todo.db"
        val DB_VERSION = 1
    }

    class TaskEntry : BaseColumns {

        companion object {
            val TABLE = "todo"
            val COL_TODO_TITLE = "title"
            val _ID = BaseColumns._ID
        }
    }
}