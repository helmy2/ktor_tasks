package com.example.data.model

data class Task(
    val id: Int,
    val listId: Int,
    val title: String,
    val description: String,
    val date: Long
)