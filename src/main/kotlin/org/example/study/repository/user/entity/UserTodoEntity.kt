package org.example.study.repository.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.example.study.domain.model.Todo
import org.example.study.domain.model.TodoDescription
import org.example.study.domain.model.TodoTitle
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "user_todos")
data class UserTodoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    val userId: UUID,
    @Column(nullable = false)
    val title: String,
    @Column(nullable = false)
    val description: String,
    @Column(nullable = false)
    val createdAt: LocalDateTime,
    @Column(nullable = false)
    val updatedAt: LocalDateTime,
    @Column
    val completedAt: LocalDateTime? = null,
) {
    fun toDomainModel(): Todo {
        return Todo(
            id = id!!,
            title = TodoTitle.of(title),
            description = TodoDescription.of(description),
            createdAt = createdAt,
            updatedAt = updatedAt,
            completedAt = completedAt,
        )
    }

    companion object {
        fun fromDomainModel(
            userId: UUID,
            todo: Todo,
        ): UserTodoEntity {
            return UserTodoEntity(
                id = if (todo.id == 0L) null else todo.id,
                userId = userId,
                title = todo.title.value,
                description = todo.description.value,
                createdAt = todo.createdAt,
                updatedAt = todo.updatedAt,
                completedAt = todo.completedAt,
            )
        }
    }
}
