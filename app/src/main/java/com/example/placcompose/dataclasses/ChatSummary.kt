package com.example.placcompose.dataclasses

data class ChatSummary(
    val chatId: String,
    val otherUserId: String,
    val userName: String,
    val profilePictureUrl: String
)