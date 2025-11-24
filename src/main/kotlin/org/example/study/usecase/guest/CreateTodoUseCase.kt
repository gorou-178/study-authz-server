package org.example.study.usecase.guest

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.study.domain.error.AppError
import org.example.study.domain.model.Todo
import org.example.study.domain.model.TodoDescription
import org.example.study.domain.model.TodoTitle
import org.example.study.repository.guest.TodoRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CreateTodoUseCase(
    private val todoRepository: TodoRepository,
) {
    fun execute(
        title: String,
        description: String,
    ): Result<Todo, AppError> {
        return try {
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

            // Entityに変換して保存
            val entity = org.example.study.repository.guest.entity.TodoEntity.fromDomainModel(newTodo)
            val savedEntity = todoRepository.save(entity)
            Ok(savedEntity.toDomainModel())
        } catch (e: IllegalArgumentException) {
            // バリデーションエラー（値オブジェクトのof()で発生）
            Err(AppError.ClientError.ValidationError(e.message ?: "Invalid input", e))
        } catch (e: Exception) {
            // データベースエラーやその他の予期しないエラー
            Err(AppError.ServerError.DatabaseError("Failed to create todo", e))
        }
    }
}
