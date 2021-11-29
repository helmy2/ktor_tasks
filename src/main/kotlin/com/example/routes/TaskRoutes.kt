package com.example.routes

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
    repository:TaskRepository,
) {

    authenticate("jwt"){

        post("/v1/tasks/create"){

            val note = try {
                call.receive<Task>()
            } catch (e:Exception){
                call.respond(HttpStatusCode.BadRequest,"Missing Fields")
                return@post
            }

            try {
                val email = call.principal<User>()!!.email
                repository.addTask(note,email)
                call.respond(HttpStatusCode.OK,"Note Added Successfully!")
            } catch (e:Exception){
                call.respond(HttpStatusCode.Conflict,e.message ?: "Some Problem Occurred!")
            }

        }


        get("/v1/tasks"){

            try {
                val email = call.principal<User>()!!.email
                val notes = repository.getAllTask(email)
                call.respond(HttpStatusCode.OK,notes)
            } catch (e:Exception){

                call.respond(HttpStatusCode.Conflict, emptyList<Task>())
            }
        }



        post("/v1/tasks/update") {

            val note = try {
                call.receive<Task>()
            } catch (e:Exception){
                call.respond(HttpStatusCode.BadRequest,"Missing Fields")
                return@post
            }

            try {

                val email = call.principal<User>()!!.email
                repository.updateTask(note,email)
                call.respond(HttpStatusCode.OK,"Note Updated Successfully!")

            } catch (e:Exception){
                call.respond(HttpStatusCode.Conflict,e.message ?: "Some Problem Occurred!")
            }

        }


        delete("/v1/tasks/delete") {

            val noteId = try{
                call.request.queryParameters["id"]!!
            }catch (e:Exception){
                call.respond(HttpStatusCode.BadRequest,"QueryParameter:id is not present")
                return@delete
            }
            try {
                val email = call.principal<User>()!!.email
                repository.deleteTask(noteId,email)
                call.respond(HttpStatusCode.OK,"Note Deleted Successfully!")

            } catch (e:Exception){
                call.respond(HttpStatusCode.Conflict,e.message ?: "Some problem Occurred!")
            }

        }



    }

}