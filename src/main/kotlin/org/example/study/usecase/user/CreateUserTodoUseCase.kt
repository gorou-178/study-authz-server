package org.example.study.usecase.user

import org.example.study.domain.model.Todo
import org.example.study.domain.model.TodoDescription
import org.example.study.domain.model.TodoTitle
import org.example.study.repository.user.UserTodoRepository
import org.example.study.repository.user.saveTodo
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class CreateUserTodoUseCase(
    private val userTodoRepository: UserTodoRepository,
) {
    fun execute(
        userId: UUID,
        title: String,
        description: String,
    ): Result<Todo> {
        return runCatching {
            // 入力のサニタイゼーション（トリミング）
            val sanitizedTitle = title.trim()
            val sanitizedDescription = description.trim()

            // ドメインモデルの作成（値オブジェクトでバリデーション）
            val todoTitle = TodoTitle.of(sanitizedTitle)
            val todoDescription = TodoDescription.of(sanitizedDescription)

            val now = LocalDateTime.now()
            // 保存時にIDは自動採番される
            val newTodo =
                Todo(
                    id = 0L,
                    title = todoTitle,
                    description = todoDescription,
                    isCompleted = false,
                    createdAt = now,
                    updatedAt = now,
                    completedAt = null,
                )

            userTodoRepository.saveTodo(userId, newTodo)
        }
    }
}
