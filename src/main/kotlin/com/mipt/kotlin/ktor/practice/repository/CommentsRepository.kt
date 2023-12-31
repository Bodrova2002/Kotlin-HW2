package com.mipt.kotlin.ktor.practice.repository

import com.mipt.kotlin.ktor.practice.model.Comment

interface CommentsRepository {

    fun getAll(): Collection<Comment>

    fun getById(id: Long): Comment?

    fun createComment(commentText: String): Comment

    fun updateComment(id: Long, text: String): Comment?

    fun deleteComment(id: Long): Boolean
}