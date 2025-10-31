package org.example.study.usecase

import org.example.study.domain.model.Todo
import org.example.study.domain.repository.TodoRepository
import org.springframework.stereotype.Service

@Service
class GetRandomTodoUseCase(
    private val todoRepository: TodoRepository,
) {
    fun execute(): Todo? {
        return todoRepository.findRandomTodo()
    }
}
