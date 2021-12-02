package com.example.data.model

data class Task(
    val id: String,
    val listId: Int,
    val title: String,
    val description: String,
    val date: Long
)