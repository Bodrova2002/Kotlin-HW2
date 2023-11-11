package com.mipt.kotlin.ktor.practice.repository.impl

import com.mipt.kotlin.ktor.practice.model.Comment
import com.mipt.kotlin.ktor.practice.repository.CommentsRepository
import java.time.Instant
import kotlin.random.Random

class DefaultCommentsRepository: CommentsRepository {

    private val comments: MutableSet<Comment> = mutableSetOf()

    override fun getAll(): Collection<Comment> {
        return comments.toList()
    }

    override fun getById(id: Long): Comment? {
        return comments.find { it.id == id.toString() }
    }

    override fun createComment(commentText: String): Comment {
        val createdComment = Comment(
            id = Random.nextLong().toString(),
            text = commentText,
            createdAt = Instant.now().toEpochMilli(),
            updatedAt = Instant.now().toEpochMilli()
        )
        comments.add(createdComment)
        return createdComment
    }

    override fun updateComment(id: Long, text: String): Comment? {
        val comment = comments.find { it.id == id.toString() }
        if (comment != null) {
            val updatedComment = comment.copy(text = text, updatedAt = Instant.now().toEpochMilli())
            comments.remove(comment)
            comments.add(updatedComment)
            return updatedComment
        }
        return null
    }

    override fun deleteComment(id: Long): Boolean {
        val comment = comments.find { it.id == id.toString() }
        if (comment != null) {
            comments.remove(comment)
            return true
        }
        return false
    }
}
