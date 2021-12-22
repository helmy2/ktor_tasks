package com.example.data.model

import io.ktor.auth.*

data class User(
    val email: String,
    val name: String,
) : Principal