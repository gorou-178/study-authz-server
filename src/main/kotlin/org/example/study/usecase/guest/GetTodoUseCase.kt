package org.example.study.usecase.guest

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.study.domain.error.AppError
import org.example.study.domain.model.Todo
import org.example.study.repository.guest.TodoRepository
import org.example.study.repository.guest.findTodosWithinLastMonth
import org.springframework.stereotype.Service

@Service
class GetTodoUseCase(
    private val todoRepository: TodoRepository,
) {
    fun execute(): Result<List<Todo>, AppError> {
        return try {
            val todos = todoRepository.findTodosWithinLastMonth()
            Ok(todos.ifEmpty { emptyList() })
        } catch (e: Exception) {
            Err(AppError.ServerError.DatabaseError("Failed to fetch todos", e))
        }
    }
}
