package com.example

import com.example.authentication.JwtService
import com.example.authentication.hash
import com.example.repository.DatabaseFactory
import com.example.repository.ListRepository
import com.example.repository.TaskRepository
import com.example.repository.UserRepository
import com.example.routes.listRoutes
import com.example.routes.taskRoutes
import com.example.routes.userRoutes
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module(testing: Boolean = false) {

    DatabaseFactory.init()
    val userRepository = UserRepository()
    val taskRepository = TaskRepository()
    val listRepository = ListRepository()
    val jwtService = JwtService()
    val hashFunction = { s: String -> hash(s) }


    install(Authentication) {

        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "Note Server"
            validate {
                val payload = it.payload
                val email = payload.getClaim("email").asString()
                val user = userRepository.findUserByEmail(email)
                user
            }
        }

    }

    install(ContentNegotiation) {
        gson {}
    }

    routing {
        userRoutes(userRepository, jwtService, hashFunction)
        taskRoutes(taskRepository)
        listRoutes(listRepository)
    }
}

