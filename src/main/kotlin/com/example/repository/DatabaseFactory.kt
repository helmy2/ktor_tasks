package com.example.repository

import com.example.data.table.TaskListTable
import com.example.data.table.TaskTable
import com.example.data.table.UserTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        Database.connect(
            url = System.getenv("URL"),
            user = System.getenv("USER"),
            password = System.getenv("PASSWORD"),
        )

        transaction {
            SchemaUtils.create(UserTable)
            SchemaUtils.create(TaskListTable)
            SchemaUtils.create(TaskTable)
        }
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}