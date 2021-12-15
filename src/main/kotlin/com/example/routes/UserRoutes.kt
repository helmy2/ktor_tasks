package com.example.routes

import com.example.authentication.JwtService
import com.example.data.model.*
import com.example.repository.UserRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
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
            call.respond(HttpStatusCode.BadRequest, Response(false, e.message + "Missing Some Fields"))
            return@post
        }

        try {
            val user = LocalUser(registerRequest.email, hashFunction(registerRequest.password), registerRequest.name)
            if (repository.findUserByEmail(registerRequest.email) != null)
                call.respond(HttpStatusCode.Conflict, Response(false, "Email is already exist"))
            else {
                repository.addUser(user)
                call.respond(HttpStatusCode.OK, Response(true, jwtService.generateToken(user)))
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, Response(false, e.message ?: "Some Problem Occurred!"))
        }
    }

    post("/v1/users/login") {
        val loginRequest = try {
            call.receive<LoginRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, Response(false, "Missing Some Fields"))
            return@post
        }

        try {
            val user = repository.findUserByEmail(loginRequest.email)

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, AccountResponse(false, null, "Wrong Email Id"))
            } else {

                if (user.hashPassword == hashFunction(loginRequest.password)) {
                    call.respond(
                        HttpStatusCode.OK,
                        AccountResponse(
                            true,
                            User(email = user.email, name = user.name),
                            jwtService.generateToken(user)
                        )
                    )
                } else {
                    call.respond(HttpStatusCode.BadRequest, AccountResponse(false, null, "Password Incorrect!"))
                }
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, AccountResponse(false, null, e.message ?: "Some Problem Occurred!"))
        }
    }
}











