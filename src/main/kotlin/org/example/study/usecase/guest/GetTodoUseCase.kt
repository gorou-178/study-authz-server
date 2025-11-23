package org.example.study.usecase.guest

import org.example.study.domain.model.Todo
import org.example.study.repository.guest.TodoRepository
import org.example.study.repository.guest.findTodo
import org.springframework.stereotype.Service

@Service
class GetTodoUseCase(
    private val todoRepository: TodoRepository,
) {
    fun execute(): List<Todo> {
        val todo = todoRepository.findTodo()
        return if (todo != null) listOf(todo) else emptyList()
    }
}
