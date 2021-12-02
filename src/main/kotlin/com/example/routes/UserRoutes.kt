package com.example.routes

import com.example.authentication.JwtService
import com.example.data.model.LoginRequest
import com.example.data.model.RegisterRequest
import com.example.data.model.User
import com.example.repository.UserRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.awt.Image
import java.io.File

fun Route.userRoutes(
    repository: UserRepository,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {

    post("/v1/users/register") {
        val registerRequest = try {
            call.receive<RegisterRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, e.message + "Missing Some Fields")
            return@post
        }

        try {
            val user = User(registerRequest.email, hashFunction(registerRequest.password), registerRequest.name)
            if (repository.findUserByEmail(registerRequest.email) != null)
                call.respond(HttpStatusCode.Conflict, "Email is already exist")
            else {
                repository.addUser(user)
                call.respond(HttpStatusCode.OK, jwtService.generateToken(user))
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, e.message ?: "Some Problem Occurred!")
        }
    }

    post("/v1/users/login") {
        val loginRequest = try {
            call.receive<LoginRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Missing Some Fields")
            return@post
        }

        try {
            val user = repository.findUserByEmail(loginRequest.email)

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Wrong Email Id")
            } else {

                if (user.hashPassword == hashFunction(loginRequest.password)) {
                    call.respond(HttpStatusCode.OK, jwtService.generateToken(user))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Password Incorrect!")
                }
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, e.message ?: "Some Problem Occurred!")
        }
    }

    authenticate("jwt") {
        post("/v1/users/image") {
            val multipartData = call.receiveMultipart()
            multipartData.forEachPart { part ->
                if (part is PartData.FileItem && part.contentType!!.match(ContentType.Image.Any)) {
                    val email = call.principal<User>()!!.email
                    val fileName = "$email.${part.contentType!!.contentSubtype}"
                    val fileBytes = part.streamProvider().readBytes()
                    File("src/main/resources/static/$fileName").writeBytes(fileBytes)
                    repository.updateProfileImage(fileName,email)
                }
            }
        }
    }
}











