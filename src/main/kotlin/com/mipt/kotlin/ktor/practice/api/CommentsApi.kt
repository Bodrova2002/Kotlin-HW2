package com.mipt.kotlin.ktor.practice.api

import com.mipt.kotlin.ktor.practice.api.model.CreateCommentRequest
import com.mipt.kotlin.ktor.practice.repository.CommentsRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import com.mipt.kotlin.ktor.practice.model.Comment
import io.ktor.server.routing.patch
import io.ktor.server.routing.delete

fun Application.commentsApi() {
    val commentsRepository by inject<CommentsRepository>()

    routing {
        // Создание комментария
        put("/comment/create") {
            val request = call.receive<CreateCommentRequest>()

            val createdComment = commentsRepository.createComment(request.commentText)

            call.respond(createdComment)
        }

        // Редактирование комментария
        patch("/comment/{id}/update") {
            val commentId = call.parameters["id"]?.toLongOrNull()

            if (commentId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid comment ID")
                return@patch
            }

            val request = call.receive<CreateCommentRequest>()
            val updatedComment = commentsRepository.updateComment(commentId, request.commentText)

            if (updatedComment != null) {
                call.respond(HttpStatusCode.OK, updatedComment)
            } else {
                call.respond(HttpStatusCode.NotFound, "Comment not found")
            }
        }

        // Получение списка всех комментариев
        get("/comments/all") {
            val comments = commentsRepository.getAll()
            call.respond(comments)
        }

        // Получение комментария по ID
        get("/comment/{id}/get") {
            val commentId = call.parameters["id"]?.toLongOrNull()

            if (commentId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid comment ID")
                return@get
            }

            val comment = commentsRepository.getById(commentId)

            if (comment != null) {
                call.respond(HttpStatusCode.OK, comment)
            } else {
                call.respond(HttpStatusCode.NotFound, "Comment not found")
            }
        }

        // Удаление комментария (опционально)
        delete("/comment/{id}/delete") {
            val commentId = call.parameters["id"]?.toLongOrNull()

            if (commentId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid comment ID")
                return@delete
            }

            val deleted = commentsRepository.deleteComment(commentId)

            if (deleted) {
                call.respond(HttpStatusCode.OK, "Comment deleted")
            } else {
                call.respond(HttpStatusCode.NotFound, "Comment not found")
            }
        }
    }
}
