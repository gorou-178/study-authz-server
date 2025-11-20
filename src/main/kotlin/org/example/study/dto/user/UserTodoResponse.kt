package org.example.study.dto.user

import org.example.study.domain.model.Todo
import java.time.LocalDateTime

data class UserTodoResponse(
    val id: Long,
    val title: String,
    val description: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val completedAt: LocalDateTime?,
) {
    companion object {
        fun from(todo: Todo): UserTodoResponse {
            return UserTodoResponse(
                id = todo.id,
                title = todo.title.value,
                description = todo.description.value,
                createdAt = todo.createdAt,
                updatedAt = todo.updatedAt,
                completedAt = todo.completedAt,
            )
        }
    }
}
