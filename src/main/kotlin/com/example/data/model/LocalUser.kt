package com.example.data.model

import io.ktor.auth.*

data class LocalUser(
    val email: String,
    val hashPassword: String,
    val name: String,
) : Principal