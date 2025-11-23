package org.example.study.repository.guest.entity

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

@Entity
@Table(name = "todos")
data class TodoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    val title: String,
    @Column(nullable = false)
    val description: String,
    @Column(nullable = false)
    val isCompleted: Boolean = false,
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
            isCompleted = isCompleted,
            createdAt = createdAt,
            updatedAt = updatedAt,
            completedAt = completedAt,
        )
    }

    companion object {
        fun fromDomainModel(todo: Todo): TodoEntity {
            return TodoEntity(
                id = todo.id,
                title = todo.title.value,
                description = todo.description.value,
                isCompleted = todo.isCompleted,
                createdAt = todo.createdAt,
                updatedAt = todo.updatedAt,
                completedAt = todo.completedAt,
            )
        }
    }
}
