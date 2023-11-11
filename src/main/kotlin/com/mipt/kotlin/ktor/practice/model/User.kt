package com.mipt.kotlin.ktor.practice.model
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val password: String,
    val name: String,
    val profileCreated: Long
)
