package org.example.study.repository.user

import org.example.study.domain.model.Todo
import java.util.UUID

interface UserTodoRepository {
    fun findByUserId(userId: UUID): List<Todo>

    fun save(
        userId: UUID,
        todo: Todo,
    ): Todo
}
