package com.example.snake2025.models

import com.google.firebase.Timestamp

data class Score(
    val uid: String = "",
    val username: String = "",
    val score: Long = 0,
    val timestamp: Timestamp? = null
)
