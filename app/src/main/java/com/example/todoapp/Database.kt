package com.todoapp.todo.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Database(context: Context) : SQLiteOpenHelper(context, Todo.DB_NAME, null, Todo.DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE " + Todo.TodoEntry.TABLE + " ( " +
                Todo.TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Todo.TodoEntry.COL_TODO_TITLE + " TEXT NOT NULL);"

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + Todo.TodoEntry.TABLE)
        onCreate(db)
    }
}