package com.example.data.table

import org.jetbrains.exposed.sql.Table

object TaskListTable : Table() {
    val id = integer("id").autoIncrement()
    val userEmail = varchar("userEmail", 512).references(UserTable.email)
    val title = text("title")
    val description = text("description")
    val color = text("color")
    val date = long("date")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}