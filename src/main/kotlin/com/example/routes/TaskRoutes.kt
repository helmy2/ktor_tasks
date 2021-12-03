package com.example.routes

import com.example.data.model.Response
import com.example.data.model.TaskList
import com.example.data.model.Task
import com.example.data.model.User
import com.example.repository.TaskRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.taskRoutes(
    repository: TaskRepository,
) {

    authenticate("jwt") {

        post("/v1/tasks/create") {

            val task = try {
                call.receive<Task>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, Response(false, "Missing Fields"))
                return@post
            }

            try {
                val email = call.principal<User>()!!.email
                repository.addTask(task, email)
                call.respond(HttpStatusCode.OK, "Task Added Successfully!")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, Response(false, e.message ?: "Some Problem Occurred!"))
            }

        }

        post("/v1/tasks/update") {

            val task = try {
                call.receive<Task>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, Response(false, "Missing Fields"))
                return@post
            }

            try {

                val email = call.principal<User>()!!.email
                repository.updateTask(task, email)
                call.respond(HttpStatusCode.OK, Response(true, "Task Updated Successfully!"))

            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, Response(false, e.message ?: "Some Problem Occurred!"))
            }

        }


        delete("/v1/tasks/delete") {

            val taskId = try {
                call.request.queryParameters["id"]!!.toInt()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, Response(false, "QueryParameter:id is not present"))
                return@delete
            }
            try {
                val email = call.principal<User>()!!.email
                repository.deleteTask(taskId, email)
                call.respond(HttpStatusCode.OK, Response(true, "Task Deleted Successfully!"))

            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, Response(false, e.message ?: "Some problem Occurred!"))
            }

        }


    }

}