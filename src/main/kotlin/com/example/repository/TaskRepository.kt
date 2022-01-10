package com.example.repository

import com.example.data.model.Task
import com.example.data.model.TaskList
import com.example.data.table.TaskListTable
import com.example.data.table.TaskTable
import org.jetbrains.exposed.sql.*

class TaskRepository {

    suspend fun addTask(task: Task, email: String) {
        DatabaseFactory.dbQuery {
            if (task.id == null)
                TaskTable.insert {
                    it[listId] = task.listId
                    it[userEmail] = email
                    it[title] = task.title
                    it[done] = task.done
                    it[description] = task.description
                    it[date] = task.date
                }
            else
                TaskTable.update(
                    where = {
                        TaskTable.userEmail.eq(email) and TaskTable.id.eq(task.id)
                    }
                ) {
                    it[listId] = task.listId
                    it[title] = task.title
                    it[done] = task.done
                    it[description] = task.description
                    it[date] = task.date
                }
        }
    }

    suspend fun deleteTask(id: Int, email: String) {
        DatabaseFactory.dbQuery {
            TaskTable.deleteWhere { TaskTable.userEmail.eq(email) and TaskTable.id.eq(id) }
        }
    }

    suspend fun getTask(email: String, taskId: Int): Task {
        val task = DatabaseFactory.dbQuery {
            TaskTable.select { TaskTable.userEmail.eq(email) and TaskTable.id.eq(taskId) }
                .firstNotNullOf { rowToTask(it, color = "") }
        }
        task.color = DatabaseFactory.dbQuery {
            TaskListTable.select {
                TaskListTable.userEmail.eq(email) and TaskListTable.id.eq(task.listId)
            }.first()[TaskListTable.color].toString()
        }

        return task
    }

    suspend fun search(title: String, email: String): List<TaskList> {
        val list = DatabaseFactory.dbQuery {
            TaskTable.select { TaskTable.userEmail.eq(email) and TaskTable.title.like("%$title%") }
                .mapNotNull { rowToTask(it, "") }
        }
        val taskList: MutableList<TaskList> = mutableListOf()

        list.groupBy { it.listId }.forEach {
            val tl = DatabaseFactory.dbQuery {
                TaskListTable.select { TaskListTable.userEmail.eq(email) and TaskListTable.id.eq(it.key) }
                    .firstNotNullOf { rowToList(it) }
            }
            tl.list = it.value.map {
                it.color = tl.color
                it
            }
            taskList.add(tl)
        }
        return taskList
    }

    private fun rowToTask(row: ResultRow?, color: String): Task? = row?.let {
        Task(
            id = row[TaskTable.id],
            listId = row[TaskTable.listId],
            title = row[TaskTable.title],
            done = row[TaskTable.done],
            description = row[TaskTable.description],
            color = color,
            date = row[TaskTable.date],
        )
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

}