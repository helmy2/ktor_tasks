package com.example

import com.example.authentication.JwtService
import com.example.authentication.hash
import com.example.data.table.UserTable
import com.example.repository.DatabaseFactory
import com.example.repository.Repository
import com.example.routes.userRoutes
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.sessions.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    DatabaseFactory.init()
    val db = Repository()
    val jwtService = JwtService()
    val hashFunction = { s: String -> hash(s) }

    transaction {
        SchemaUtils.create(UserTable)
    }

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {

        install(Sessions) {
            cookie<MySession>("MY_SESSION") {
                cookie.extensions["SameSite"] = "lax"
            }
        }

        install(Authentication) {

            jwt("jwt") {
                verifier(jwtService.varifier)
                realm = "Note Server"
                validate {
                    val payload = it.payload
                    val email = payload.getClaim("email").asString()
                    val user = db.findUserByEmail(email)
                    user
                }
            }

        }

        install(ContentNegotiation) {
            gson {}
        }

        routing {
            userRoutes(db,jwtService,hashFunction)
        }
    }.start(wait = true)
}

data class MySession(val count: Int = 0)
