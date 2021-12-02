package com.example.data.table

import org.jetbrains.exposed.sql.Table

object TaskTable : Table() {
    val id = integer("id").autoIncrement()
    val userEmail = varchar("userEmail", 512).references(UserTable.email)
    val listId = integer("listId").references(TaskListTable.id)
    val title = text("title")
    val description = text("description")
    val date = long("date")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}