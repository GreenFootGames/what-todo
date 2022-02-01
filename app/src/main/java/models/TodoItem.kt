package models

data class TodoItem (
        var name: String,
        var isChecked: Boolean = false,
        val id: Int = -1
        )

