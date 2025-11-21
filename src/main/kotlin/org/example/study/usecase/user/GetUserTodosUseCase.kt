package org.example.study.usecase.user

import org.example.study.domain.model.Todo
import org.example.study.repository.user.UserTodoRepository
import org.example.study.repository.user.findUserTodosSorted
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetUserTodosUseCase(
    private val userTodoRepository: UserTodoRepository,
) {
    fun execute(userId: UUID): List<Todo> {
        return userTodoRepository.findUserTodosSorted(userId)
    }
}
