package com.example.repository

import com.example.data.model.Task
import com.example.data.table.TaskTable
import com.example.data.table.TaskTable.date
import com.example.data.table.TaskTable.description
import com.example.data.table.TaskTable.id
import com.example.data.table.TaskTable.listId
import com.example.data.table.TaskTable.title
import org.jetbrains.exposed.sql.*

class TaskRepository {

    suspend fun addTask(task: Task, email: String) {
        DatabaseFactory.dbQuery {
            TaskTable.insert {
                it[id] = task.id
                it[listId] = task.listId
                it[userEmail] = email
                it[title] = task.title
                it[description] = task.description
                it[date] = task.date
            }
        }
    }


    suspend fun updateTask(task: Task, email: String) {

        DatabaseFactory.dbQuery {

            TaskTable.update(
                where = {
                    TaskTable.userEmail.eq(email) and TaskTable.id.eq(task.id)
                }
            ) {
                it[listId] = task.listId
                it[title] = task.title
                it[description] = task.description
                it[date] = task.date
            }

        }

    }

    suspend fun deleteTask(id: String, email: String) {
        DatabaseFactory.dbQuery {
            TaskTable.deleteWhere { TaskTable.userEmail.eq(email) and TaskTable.id.eq(id) }
        }
    }



}