package com.example.routes

import com.example.data.model.Task
import com.example.data.model.TaskList
import com.example.data.model.User
import com.example.repository.ListRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.listRoutes(
    repository: ListRepository,
) {

    authenticate("jwt") {


        get("/v1/list") {

            try {
                val email = call.principal<User>()!!.email
                val list = repository.getAllList(email)
                call.respond(HttpStatusCode.OK, list)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, emptyList<Task>())
            }
        }

        get("/v1/list/tasks") {
            val listId = try {
                call.request.queryParameters["id"]!!.toInt()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "QueryParameter:id is not present")
                return@get
            }

            try {
                val email = call.principal<User>()!!.email
                val list = repository.getListTasks(listId,email)
                call.respond(HttpStatusCode.OK, list)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, emptyList<Task>())
            }
        }


        post("/v1/list/update") {

            val list = try {
                call.receive<TaskList>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Missing Fields")
                return@post
            }

            try {

                val email = call.principal<User>()!!.email
                repository.updateList(list, email)
                call.respond(HttpStatusCode.OK, "list Updated Successfully!")

            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, e.message ?: "Some Problem Occurred!")
            }

        }


        delete("/v1/list/delete") {

            val listId = try {
                call.request.queryParameters["id"]!!.toInt()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "QueryParameter:id is not present")
                return@delete
            }
            try {
                val email = call.principal<User>()!!.email
                repository.deleteList(listId, email)
                call.respond(HttpStatusCode.OK, "List Deleted Successfully!")

            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, e.message ?: "Some problem Occurred!")
            }

        }

        post("/v1/list/create") {

            val list = try {
                call.receive<TaskList>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Missing Fields")
                return@post
            }
            try {
                val email = call.principal<User>()!!.email
                repository.creatList(list, email)
                call.respond(HttpStatusCode.OK, "List Created Successfully!")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, e.message ?: "Some Problem Occurred!")
            }

        }

    }

}