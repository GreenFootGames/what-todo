package com.greenfootgames.todolistapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import models.TodoItem

class DBHandler(context: Context) :
        SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    private companion object {
        private const val DB_NAME = "TodoList"
        private const val DB_VERSION = 1
        private const val TABLE_TODOS = "Todos"
        private const val ID_KEY = "_id"
        private const val NAME_KEY = "name"
        private const val IS_CHECKED_KEY = "isChecked"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTodosTable =
                "CREATE TABLE $TABLE_TODOS (" +
                "$ID_KEY INTEGER PRIMARY KEY," +
                "$NAME_KEY TEXT," +
                "$IS_CHECKED_KEY INTEGER" +
                ")"
        db?.execSQL(createTodosTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Will not change table layout nor will add new tables
    }

    fun addTodo(newItem: TodoItem) : Long {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(NAME_KEY, newItem.name)
        cv.put(IS_CHECKED_KEY, newItem.isChecked.toInt())

        val success = db.insert(TABLE_TODOS, null, cv)

        db.close()
        return success

    }

    fun deleteTodo(item: TodoItem): Int {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(ID_KEY, item.id)

        val success = db.delete(TABLE_TODOS, "$ID_KEY=${item.id}", null)
        db.close()
        return success
    }

    fun changeIsChecked(todo: TodoItem) : Int {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(IS_CHECKED_KEY, todo.isChecked.toInt())

        val success = db.update(TABLE_TODOS, cv, "$ID_KEY=${todo.id}", null)
        db.close()
        return success
    }

    fun viewTodos() : ArrayList<TodoItem> {
        val todoList: ArrayList<TodoItem> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_TODOS"

        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var name: String
        var isChecked: Int

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(ID_KEY))
                name = cursor.getString(cursor.getColumnIndex(NAME_KEY))
                isChecked = cursor.getInt(cursor.getColumnIndex(IS_CHECKED_KEY))

                val todo = TodoItem(name, isChecked.toBoolean(), id)
                todoList.add(todo)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return todoList
    }

    // There is no built-in BOOL type in SQLite
    private fun Boolean.toInt() = if (this) 1 else 0
    private fun Int.toBoolean(): Boolean = this == 1
}

