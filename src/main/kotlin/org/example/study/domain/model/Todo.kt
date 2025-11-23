package org.example.study.domain.model

import java.time.LocalDateTime

data class Todo(
    val id: Long,
    val title: TodoTitle,
    val description: TodoDescription,
    val isCompleted: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val completedAt: LocalDateTime?,
)
