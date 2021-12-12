package com.example.data.model

import io.ktor.auth.*

data class AccountResponse(
    val success: Boolean,
    val user: User?,
    val message: String?
) : Principal

