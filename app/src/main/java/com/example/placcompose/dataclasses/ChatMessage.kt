package com.example.placcompose.dataclasses

data class ChatMessage(
    val id: String = "",
    val text: String = "",
    val senderId: String = "",
    val receiverId: String = "", // Dodano
    val timestamp: Long = 0
)

