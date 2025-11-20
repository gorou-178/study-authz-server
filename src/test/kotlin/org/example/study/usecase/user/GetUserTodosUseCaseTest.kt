package org.example.study.usecase.user

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.example.study.domain.model.Todo
import org.example.study.domain.model.TodoDescription
import org.example.study.domain.model.TodoTitle
import org.example.study.repository.user.UserTodoRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class GetUserTodosUseCaseTest {
    private lateinit var userTodoRepository: UserTodoRepository
    private lateinit var useCase: GetUserTodosUseCase

    @BeforeEach
    fun setUp() {
        userTodoRepository = mockk()
        useCase = GetUserTodosUseCase(userTodoRepository)
    }

    @Test
    @DisplayName("指定されたユーザーIDのTodoリストを返す")
    fun execute_returnsUserTodos() {
        // Given
        val userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        val expectedTodos =
            listOf(
                Todo(
                    id = 1L,
                    title = TodoTitle.of("プロジェクト計画"),
                    description = TodoDescription.of("プロジェクトの計画を立てる"),
                    createdAt = LocalDateTime.now().minusDays(7),
                    updatedAt = LocalDateTime.now().minusDays(7),
                    completedAt = LocalDateTime.now().minusDays(6),
                ),
                Todo(
                    id = 2L,
                    title = TodoTitle.of("データベース設計"),
                    description = TodoDescription.of("ER図を作成してテーブル設計を行う"),
                    createdAt = LocalDateTime.now().minusDays(5),
                    updatedAt = LocalDateTime.now().minusDays(3),
                    completedAt = null,
                ),
            )

        every { userTodoRepository.findByUserId(userId) } returns expectedTodos

        // When
        val result = useCase.execute(userId)

        // Then
        assertThat(result).isEqualTo(expectedTodos)
        assertThat(result).hasSize(2)
        verify(exactly = 1) { userTodoRepository.findByUserId(userId) }
    }

    @Test
    @DisplayName("ユーザーにTodoが無い場合は空のリストを返す")
    fun execute_returnsEmptyListWhenNoTodos() {
        // Given
        val userId = UUID.fromString("00000000-0000-0000-0000-000000000000")
        every { userTodoRepository.findByUserId(userId) } returns emptyList()

        // When
        val result = useCase.execute(userId)

        // Then
        assertThat(result).isEmpty()
        verify(exactly = 1) { userTodoRepository.findByUserId(userId) }
    }

    @Test
    @DisplayName("異なるユーザーIDで呼び出すと異なる結果を返す")
    fun execute_returnsDifferentTodosForDifferentUsers() {
        // Given
        val userId1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        val userId2 = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8")

        val todos1 =
            listOf(
                Todo(
                    id = 1L,
                    title = TodoTitle.of("ユーザー1のTodo"),
                    description = TodoDescription.of("説明1"),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                    completedAt = null,
                ),
            )

        val todos2 =
            listOf(
                Todo(
                    id = 2L,
                    title = TodoTitle.of("ユーザー2のTodo"),
                    description = TodoDescription.of("説明2"),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                    completedAt = null,
                ),
            )

        every { userTodoRepository.findByUserId(userId1) } returns todos1
        every { userTodoRepository.findByUserId(userId2) } returns todos2

        // When
        val result1 = useCase.execute(userId1)
        val result2 = useCase.execute(userId2)

        // Then
        assertThat(result1).isEqualTo(todos1)
        assertThat(result2).isEqualTo(todos2)
        assertThat(result1).isNotEqualTo(result2)
        verify(exactly = 1) { userTodoRepository.findByUserId(userId1) }
        verify(exactly = 1) { userTodoRepository.findByUserId(userId2) }
    }

    @Test
    @DisplayName("複数のTodoを持つユーザーのリストを正しく返す")
    fun execute_returnsMultipleTodos() {
        // Given
        val userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        val expectedTodos =
            listOf(
                Todo(
                    id = 1L,
                    title = TodoTitle.of("Todo 1"),
                    description = TodoDescription.of("説明 1"),
                    createdAt = LocalDateTime.now().minusDays(3),
                    updatedAt = LocalDateTime.now().minusDays(3),
                    completedAt = null,
                ),
                Todo(
                    id = 2L,
                    title = TodoTitle.of("Todo 2"),
                    description = TodoDescription.of("説明 2"),
                    createdAt = LocalDateTime.now().minusDays(2),
                    updatedAt = LocalDateTime.now().minusDays(2),
                    completedAt = null,
                ),
                Todo(
                    id = 3L,
                    title = TodoTitle.of("Todo 3"),
                    description = TodoDescription.of("説明 3"),
                    createdAt = LocalDateTime.now().minusDays(1),
                    updatedAt = LocalDateTime.now().minusDays(1),
                    completedAt = LocalDateTime.now(),
                ),
            )

        every { userTodoRepository.findByUserId(userId) } returns expectedTodos

        // When
        val result = useCase.execute(userId)

        // Then
        assertThat(result).hasSize(3)
        assertThat(result).containsExactlyElementsOf(expectedTodos)
        assertThat(result.map { it.title.value })
            .containsExactly("Todo 1", "Todo 2", "Todo 3")
        verify(exactly = 1) { userTodoRepository.findByUserId(userId) }
    }

    @Test
    @DisplayName("RepositoryからソートされたTodoリストをそのまま返す")
    fun execute_returnsSortedTodosFromRepository() {
        // Given
        val userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        val now = LocalDateTime.now()

        // Repositoryがソート済みのリストを返す前提
        val sortedTodos =
            listOf(
                Todo(
                    id = 3L,
                    title = TodoTitle.of("最新のTodo"),
                    description = TodoDescription.of("createdAt = 1日前 completedAt = 1日前"),
                    createdAt = now.minusDays(1),
                    updatedAt = now.minusDays(1),
                    completedAt = now.minusDays(1),
                ),
                Todo(
                    id = 4L,
                    title = TodoTitle.of("最新のTodo(未完了)"),
                    description = TodoDescription.of("createdAt = 1日前 completedAt = null"),
                    createdAt = now.minusDays(1),
                    updatedAt = now.minusDays(1),
                    completedAt = null,
                ),
                Todo(
                    id = 1L,
                    title = TodoTitle.of("古いTodo"),
                    description = TodoDescription.of("createdAt = 3日前 completedAt = 2日前"),
                    createdAt = now.minusDays(3),
                    updatedAt = now.minusDays(3),
                    completedAt = now.minusDays(2),
                ),
                Todo(
                    id = 2L,
                    title = TodoTitle.of("古いTodo(未完了)"),
                    description = TodoDescription.of("createdAt = 3日前 completedAt = null"),
                    createdAt = now.minusDays(3),
                    updatedAt = now.minusDays(3),
                    completedAt = null,
                ),
            )

        every { userTodoRepository.findByUserId(userId) } returns sortedTodos

        // When
        val result = useCase.execute(userId)

        // Then
        assertThat(result).hasSize(4)
        assertThat(result).containsExactlyElementsOf(sortedTodos)
        // ソート順を検証: createdAtの降順、その後completedAtの降順（nullは最後）
        assertThat(result.map { it.title.value })
            .containsExactly(
                "最新のTodo",
                "最新のTodo(未完了)",
                "古いTodo",
                "古いTodo(未完了)",
            )
        verify(exactly = 1) { userTodoRepository.findByUserId(userId) }
    }
}
