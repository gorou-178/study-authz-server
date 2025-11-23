package org.example.study.usecase.user

import org.example.study.domain.model.Todo
import org.example.study.repository.user.UserTodoRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetUserTodosUseCase(
    private val userTodoRepository: UserTodoRepository,
) {
    fun execute(
        userId: UUID,
        page: Int = 0,
        size: Int = 20,
    ): Page<Todo> {
        val validatedSize = size.coerceIn(1, 100)
        val pageable = PageRequest.of(page, validatedSize)
        return userTodoRepository.findByUserIdOrderByIsCompletedAscCreatedAtDesc(userId, pageable)
            .map { it.toDomainModel() }
    }
}
