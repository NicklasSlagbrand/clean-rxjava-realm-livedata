package com.nicklasslagbrand.placeholder.domain.model

data class Message(
    val id: Int,
    val userId: Int,
    val title: String,
    val description: String,
    val dateStamp: String
)


