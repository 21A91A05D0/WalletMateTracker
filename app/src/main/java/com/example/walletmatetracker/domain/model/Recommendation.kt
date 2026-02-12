package com.example.walletmatetracker.domain.model


data class Recommendation(
    val title: String,
    val description: String,
    val priority: Int = 0 // higher = more important
)
