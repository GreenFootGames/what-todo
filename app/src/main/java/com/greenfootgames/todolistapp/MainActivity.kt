package com.greenfootgames.todolistapp

import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import models.TodoItem

class MainActivity : AppCompatActivity() {

    private lateinit var fabCreate: FloatingActionButton
    private lateinit var rvTodoList: RecyclerView
    private lateinit var tvEmptyList: TextView
    private lateinit var adapter: TodoAdapter
    private var todos: MutableList<TodoItem> = ArrayList()
    private var miIsChecked: Boolean = false // Create it 1 time, use it at mi_ctrl_a.
    private val handler = DBHandler(this)

    private companion object {
        private const val MIN_TITLE_LENGTH = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fabCreate = findViewById(R.id.fab_create)
        rvTodoList = findViewById(R.id.rv_todo_list)
        tvEmptyList = findViewById(R.id.tv_empty_todos)
        adapter = TodoAdapter(todos, this)
        checkRvContents()

        fabCreate.setOnClickListener {
            val alertDialogView = LayoutInflater.from(this).inflate(R.layout.create_title, null)
            AlertDialog.Builder(this)
                    .setView(alertDialogView)
                    .setPositiveButton("Add") { _, _ ->
                        val etTodoTitle =
                            alertDialogView.findViewById<EditText>(R.id.et_todo_title).text.toString()

                            createTodo(etTodoTitle)

                    }
                    .setNegativeButton("Cancel", null).show()
        }

    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.mi_remove_checked -> {
                for (delete in adapter.getRemoved())
                    handler.deleteTodo(delete)
                adapter.removeChecked()
                checkRvContents()

                return true
            }

            R.id.mi_ctrl_a -> {
                // miIsChecked is changed as the icon,
                //  basically own way of image detection.

                if (!miIsChecked) {
                    adapter.checkAll()
                    item.setIcon(R.drawable.ic_check_all)
                    item.title = "Uncheck all"
                    miIsChecked = true
                } else if (miIsChecked || numOfCompletedTodos() == todos.size) {
                    adapter.uncheckAll()
                    item.setIcon(R.drawable.ic_uncheck_all)
                    item.title = "Check all"
                    miIsChecked = false
                }

                return true
            }

            else -> return true
        }
    }

    private fun createTodo(etTodoTitle: String) {
        if (etTodoTitle.length >= MIN_TITLE_LENGTH) {
            adapter.addTodo(TodoItem(etTodoTitle))
            handler.addTodo(TodoItem(etTodoTitle, false, 0))

            checkRvContents()
        } else
            Toast.makeText(this, "Todo needs to be minimum 4 letters!", Toast.LENGTH_SHORT).show()

    }

    private fun numOfCompletedTodos(): Int {
        var result = 0
        for (item in todos) {
            if (item.isChecked)
                ++result
        }
        return result
    }

    private fun checkRvContents() {
        // Use to check memory and change UI at 0 items

        if (getItems().size > 0) {
            rvTodoList.visibility = View.VISIBLE
            tvEmptyList.visibility = View.GONE

            rvTodoList.layoutManager = LinearLayoutManager(this)
            adapter = TodoAdapter(getItems(), this)

            rvTodoList.adapter = adapter


        } else {
            rvTodoList.visibility = View.GONE
            tvEmptyList.visibility = View.VISIBLE

        }
    }

    private fun getItems() : ArrayList<TodoItem> {
        val handler = DBHandler(this)
        return handler.viewTodos()
    }
}
