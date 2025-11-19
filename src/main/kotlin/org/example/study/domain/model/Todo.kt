package org.example.study.domain.model

import java.time.LocalDateTime

data class Todo(
    val id: Long,
    val title: TodoTitle,
    val description: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val completedAt: LocalDateTime?,
)
