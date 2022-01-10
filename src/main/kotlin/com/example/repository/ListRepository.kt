package com.example.repository

import com.example.data.model.Task
import com.example.data.model.TaskList
import com.example.data.table.TaskListTable
import com.example.data.table.TaskTable
import com.example.data.table.TaskTable.done
import com.example.data.table.TaskTable.listId
import org.jetbrains.exposed.sql.*
import java.util.*

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
        val list = TaskListTable.select {
            TaskListTable.userEmail.eq(email)
        }.mapNotNull { rowToList(it) }

        list.forEach { taskList ->
            taskList.list = TaskTable.select(where = {
                TaskTable.userEmail.eq(email) and TaskTable.listId.eq(taskList.id!!)
            }).mapNotNull { rowToTask(it, taskList.color) }
        }

        list
    }


    suspend fun updateList(list: TaskList, email: String) {

        DatabaseFactory.dbQuery {
            list.id?.let {
                TaskListTable.update(where = {
                    TaskListTable.userEmail.eq(email) and TaskListTable.id.eq(list.id)
                }) {
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

    private suspend fun getListTasks(id: Int, email: String, color: String): List<Task> = DatabaseFactory.dbQuery {
        TaskTable.select(where = {
            TaskTable.userEmail.eq(email) and TaskTable.listId.eq(id)
        }).mapNotNull { rowToTask(it, color) }
    }


    suspend fun getList(listId: Int, email: String): TaskList {
        val taskList = DatabaseFactory.dbQuery {
            TaskListTable.select {
                TaskListTable.userEmail.eq(email) and TaskListTable.id.eq(listId)
            }.firstNotNullOf { rowToList(it) }
        }

        taskList.list = getListTasks(listId, email, taskList.color)
        return taskList
    }

    suspend fun getTodayList(email: String): List<Task> {
        val now = Calendar.getInstance()
        now[Calendar.HOUR_OF_DAY] = 0
        now[Calendar.MINUTE] = 0
        val startDate = now.timeInMillis
        now[Calendar.DAY_OF_MONTH] = now[Calendar.DAY_OF_MONTH] + 1
        val endDate = now.timeInMillis

        val list = DatabaseFactory.dbQuery {
            TaskTable.select(where = {
                TaskTable.userEmail.eq(email) and TaskTable.date.between(startDate, endDate)
            }).mapNotNull { rowToTask(it, color = "") }
        }

        list.forEach {
            it.color = DatabaseFactory.dbQuery {
                TaskListTable.select {
                    TaskListTable.userEmail.eq(email) and TaskListTable.id.eq(it.listId)
                }.first()[TaskListTable.color].toString()
            }
        }

        return list
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

    private fun rowToTask(row: ResultRow?, color: String): Task? = row?.let {
        Task(
            id = row[TaskTable.id],
            listId = row[listId],
            title = row[TaskTable.title],
            done = row[done],
            description = row[TaskTable.description],
            color = color,
            date = row[TaskTable.date],
        )
    }
}