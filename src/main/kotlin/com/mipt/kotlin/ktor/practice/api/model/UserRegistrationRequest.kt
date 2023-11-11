package com.mipt.kotlin.ktor.practice.api.model
import kotlinx.serialization.Serializable

@Serializable
data class UserRegistrationRequest(
    val username: String,
    val password: String,
    val name: String,
    val profileCreated: Long
    )
