package org.example.study.usecase.guest

import org.example.study.domain.model.Todo
import org.example.study.repository.guest.TodoRepository
import org.example.study.repository.guest.findTodosWithinLastMonth
import org.springframework.stereotype.Service

@Service
class GetTodoUseCase(
    private val todoRepository: TodoRepository,
) {
    fun execute(): Result<List<Todo>> {
        return runCatching {
            val todos = todoRepository.findTodosWithinLastMonth()
            todos.ifEmpty { emptyList() }
        }
    }
}
