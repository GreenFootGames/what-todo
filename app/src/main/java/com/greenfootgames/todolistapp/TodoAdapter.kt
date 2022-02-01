package com.greenfootgames.todolistapp

import android.content.Context
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.todo_item.view.*
import models.TodoItem

class TodoAdapter (private val list: MutableList<TodoItem>, private val context: Context) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    class ViewHolder(todoItem: View) : RecyclerView.ViewHolder(todoItem)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
                R.layout.todo_item,
                parent,
                false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curTodo = list[position]
        holder.itemView.apply {
            tv_todo_title.text = curTodo.name //
            cb_done.isChecked = curTodo.isChecked // Sync the UI and the list
            toggleStrikeThrough(tv_todo_title, cb_done.isChecked) //

            cb_done.setOnCheckedChangeListener { _, isChecked ->
                val handler = DBHandler(context)
                curTodo.isChecked = isChecked
                handler.changeIsChecked(curTodo)

                toggleStrikeThrough(tv_todo_title, isChecked)
            }

        }
    }

    override fun getItemCount(): Int = list.size

    private fun toggleStrikeThrough(tvTodoTitle: TextView, isChecked: Boolean) {
        if (isChecked)
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
        else
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
    }

    fun addTodo(item: TodoItem) {
        list.add(item)
        notifyItemInserted(list.size-1)
    }

    fun removeChecked() {
        // Although could be done with .removeAll(),
        // this updates the rv quicker therefore avoids speed glitch.

        val deletable: MutableList<TodoItem> = arrayListOf()
        for (item in list) {
            if (item.isChecked)
                deletable.add(item) // Put checked items in separate list
        }

        for (delete in deletable) {
            notifyItemRemoved(list.indexOf(delete))
            list.remove(delete) // Delete items in separate loop to avoid illegalAccessException

            
        }

    }

    fun getRemoved() : ArrayList<TodoItem> {
        val deletable: ArrayList<TodoItem> = ArrayList()
        for (item in list) {
            if (item.isChecked)
                deletable.add(item)
        }
        return deletable

    }

    fun checkAll() {
        for (item in list) {
            if(!item.isChecked)
                item.isChecked = true
        }
        notifyDataSetChanged()
    }

    fun uncheckAll() {
        for (item in list) {
            if (item.isChecked)
                item.isChecked = false
        }
        notifyDataSetChanged()
    }


}