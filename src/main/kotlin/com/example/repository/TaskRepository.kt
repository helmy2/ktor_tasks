package com.example.repository

import com.example.data.model.Task
import com.example.data.table.TaskTable
import com.example.data.table.TaskTable.date
import com.example.data.table.TaskTable.description
import com.example.data.table.TaskTable.id
import com.example.data.table.TaskTable.title
import org.jetbrains.exposed.sql.*

class TaskRepository {

    suspend fun addTask(note: Task, email: String) {
        DatabaseFactory.dbQuery {
            TaskTable.insert {
                it[id] = note.id
                it[userEmail] = email
                it[title] = note.title
                it[description] = note.description
                it[date] = note.date
            }

        }

    }

    suspend fun getAllTask(email: String): List<Task> = DatabaseFactory.dbQuery {

        TaskTable.select {
            TaskTable.userEmail.eq(email)
        }.mapNotNull { rowToTask(it) }

    }


    suspend fun updateTask(note: Task, email: String) {

        DatabaseFactory.dbQuery {

            TaskTable.update(
                where = {
                    TaskTable.userEmail.eq(email) and TaskTable.id.eq(note.id)
                }
            ) {
                it[title] = note.title
                it[description] = note.description
                it[date] = note.date
            }

        }

    }

    suspend fun deleteTask(id: String, email: String) {
        DatabaseFactory.dbQuery {
            TaskTable.deleteWhere { TaskTable.userEmail.eq(email) and TaskTable.id.eq(id) }
        }
    }


    private fun rowToTask(row: ResultRow?): Task? {

        if (row == null) {
            return null
        }

        return Task(
            id = row[id],
            title = row[title],
            description = row[description],
            date = row[date]
        )

    }
}