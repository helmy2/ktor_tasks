package com.example.routes

import com.example.data.model.Response
import com.example.data.model.Task
import com.example.data.model.TaskList
import com.example.data.model.LocalUser
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

        get("/v1/list/today") {
            try {
                val email = call.principal<LocalUser>()!!.email
                val taskList = repository.getTodayList(email)
                call.respond(HttpStatusCode.OK, taskList)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, emptyList<TaskList>())
            }
        }

        get("/v1/list") {
            try {
                val email = call.principal<LocalUser>()!!.email
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
                call.respond(HttpStatusCode.BadRequest, Response(false, "QueryParameter:id is not present"))
                return@get
            }

            try {
                val email = call.principal<LocalUser>()!!.email
                val taskList= repository.getList(listId, email)
                call.respond(HttpStatusCode.OK, taskList)

            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, emptyList<TaskList>())
            }
        }


        post("/v1/list/update") {

            val list = try {
                call.receive<TaskList>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, Response(false, "Missing Fields"))
                return@post
            }

            try {

                val email = call.principal<LocalUser>()!!.email
                repository.updateList(list, email)
                call.respond(HttpStatusCode.OK, Response(true, "list Updated Successfully!"))

            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, Response(false, e.message ?: "Some Problem Occurred!"))
            }

        }


        delete("/v1/list/delete") {

            val listId = try {
                call.request.queryParameters["id"]!!.toInt()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, Response(false, "QueryParameter:id is not present"))
                return@delete
            }
            try {
                val email = call.principal<LocalUser>()!!.email
                repository.deleteList(listId, email)
                call.respond(HttpStatusCode.OK, Response(true,"List Deleted Successfully!"))

            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, Response(false, e.message ?: "Some problem Occurred!"))
            }

        }

        post("/v1/list/create") {

            val list = try {
                call.receive<TaskList>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, Response(false, "Missing Fields"))
                return@post
            }
            try {
                val email = call.principal<LocalUser>()!!.email
                repository.creatList(list, email)
                call.respond(HttpStatusCode.OK, Response(true, "List Created Successfully!"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, Response(false, e.message ?: "Some Problem Occurred!"))
            }

        }

    }

}