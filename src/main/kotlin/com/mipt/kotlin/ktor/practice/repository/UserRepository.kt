package com.mipt.kotlin.ktor.practice.repository

import com.mipt.kotlin.ktor.practice.api.model.UserRegistrationRequest
import com.mipt.kotlin.ktor.practice.model.User

interface UserRepository {
    fun registerUser(userRequest: UserRegistrationRequest): User
    fun getCurrentUser(username: String, password: String): String? // Возвращаем JWT токен
    fun getUserById(userId: String): User?
}

