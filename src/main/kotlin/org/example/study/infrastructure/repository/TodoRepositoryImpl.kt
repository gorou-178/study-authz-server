package org.example.study.infrastructure.repository

import org.example.study.domain.model.Todo
import org.example.study.domain.repository.TodoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class TodoRepositoryImpl : TodoRepository {
    private val todos =
        listOf(
            Todo(
                id = 1L,
                title = "Spring Bootの学習",
                description = "Spring BootとKotlinでREST APIを作成する",
                createdAt = LocalDateTime.now().minusDays(5),
                updatedAt = LocalDateTime.now().minusDays(2),
                completedAt = null,
            ),
            Todo(
                id = 2L,
                title = "OAuth2の実装",
                description = "Keycloakを使った認証・認可の実装",
                createdAt = LocalDateTime.now().minusDays(4),
                updatedAt = LocalDateTime.now().minusDays(1),
                completedAt = null,
            ),
            Todo(
                id = 3L,
                title = "テストコードの作成",
                description = "ユニットテストと統合テストの作成",
                createdAt = LocalDateTime.now().minusDays(3),
                updatedAt = LocalDateTime.now().minusDays(3),
                completedAt = LocalDateTime.now().minusDays(1),
            ),
            Todo(
                id = 4L,
                title = "ドキュメント作成",
                description = "API仕様書とREADMEの作成",
                createdAt = LocalDateTime.now().minusDays(2),
                updatedAt = LocalDateTime.now(),
                completedAt = null,
            ),
            Todo(
                id = 5L,
                title = "Docker環境構築",
                description = "PostgreSQLとKeycloakのDocker環境構築",
                createdAt = LocalDateTime.now().minusDays(6),
                updatedAt = LocalDateTime.now().minusDays(6),
                completedAt = LocalDateTime.now().minusDays(5),
            ),
        )

    override fun findRandomTodo(): Todo? {
        return todos.randomOrNull()
    }

    override fun findAll(): List<Todo> {
        return todos
    }
}
