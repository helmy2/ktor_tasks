package com.example.repository

import com.example.data.model.Task
import com.example.data.model.TaskList
import com.example.data.table.TaskListTable
import com.example.data.table.TaskTable
import com.example.data.table.TaskTable.listId
import org.jetbrains.exposed.sql.*

class ListRepository {

    suspend fun creatList(list: TaskList, email: String) {
        DatabaseFactory.dbQuery {
            TaskListTable.insert {
                it[userEmail] = email
                it[title] = list.title
                it[description] = list.description
                it[date] = list.date
                it[color] = list.color
            }
        }
    }

    suspend fun getAllList(email: String): List<TaskList> = DatabaseFactory.dbQuery {

        TaskListTable.select {
            TaskListTable.userEmail.eq(email)
        }.mapNotNull { rowToList(it) }

    }


    suspend fun updateList(list: TaskList, email: String) {

        DatabaseFactory.dbQuery {
            list.id?.let {
                TaskListTable.update(
                    where = {
                        TaskListTable.userEmail.eq(email) and TaskListTable.id.eq(list.id)
                    }
                ) {
                    it[userEmail] = email
                    it[title] = list.title
                    it[description] = list.description
                    it[date] = list.date
                    it[color] = list.color
                }
            }
        }

    }

    suspend fun deleteList(id: Int, email: String) {
        DatabaseFactory.dbQuery {
            TaskTable.deleteWhere { TaskTable.listId.eq(id) }
            TaskListTable.deleteWhere { TaskListTable.userEmail.eq(email) and TaskListTable.id.eq(id) }
        }
    }

    suspend fun getListTasks(id: Int, email: String): List<Task> = DatabaseFactory.dbQuery {
        TaskTable.select(
            where = {
                TaskTable.userEmail.eq(email) and TaskTable.listId.eq(id)
            }
        ).mapNotNull { rowToTask(it) }
    }


    private fun rowToList(row: ResultRow?): TaskList? = row?.let {
        TaskList(
            id = row[TaskListTable.id],
            title = row[TaskListTable.title],
            description = row[TaskListTable.description],
            date = row[TaskListTable.date],
            color = row[TaskListTable.color],
        )
    }

    private fun rowToTask(row: ResultRow?): Task? = row?.let {
        Task(
            id = row[TaskTable.id],
            listId = row[listId],
            title = row[TaskTable.title],
            description = row[TaskTable.description],
            date = row[TaskTable.date]
        )
    }

}