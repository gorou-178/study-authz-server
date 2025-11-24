package org.example.study.usecase.user

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.example.study.repository.user.UserTodoRepository
import org.example.study.repository.user.entity.UserTodoEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
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
    @DisplayName("指定されたユーザーIDのTodoをページネーションで返す")
    fun execute_returnsUserTodosWithPagination() {
        // Given
        val userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        val pageable = PageRequest.of(0, 20)
        val entities =
            listOf(
                UserTodoEntity(
                    id = 1L,
                    userId = userId,
                    title = "プロジェクト計画",
                    description = "プロジェクトの計画を立てる",
                    isCompleted = true,
                    createdAt = LocalDateTime.now().minusDays(7),
                    updatedAt = LocalDateTime.now().minusDays(7),
                    completedAt = LocalDateTime.now().minusDays(6),
                ),
                UserTodoEntity(
                    id = 2L,
                    userId = userId,
                    title = "データベース設計",
                    description = "ER図を作成してテーブル設計を行う",
                    isCompleted = false,
                    createdAt = LocalDateTime.now().minusDays(5),
                    updatedAt = LocalDateTime.now().minusDays(3),
                    completedAt = null,
                ),
            )
        val page = PageImpl(entities, pageable, entities.size.toLong())

        every {
            userTodoRepository.findByUserIdOrderByIsCompletedAscCreatedAtDesc(userId, pageable)
        } returns page

        // When
        val result = useCase.execute(userId)

        // Then
        assertThat(result.content).hasSize(2)
        assertThat(result.totalElements).isEqualTo(2)
        assertThat(result.totalPages).isEqualTo(1)
        assertThat(result.number).isEqualTo(0)
        assertThat(result.size).isEqualTo(20)
        verify(exactly = 1) {
            userTodoRepository.findByUserIdOrderByIsCompletedAscCreatedAtDesc(userId, pageable)
        }
    }

    @Test
    @DisplayName("ユーザーにTodoが無い場合は空のページを返す")
    fun execute_returnsEmptyPageWhenNoTodos() {
        // Given
        val userId = UUID.fromString("00000000-0000-0000-0000-000000000000")
        val pageable = PageRequest.of(0, 20)
        val emptyPage = PageImpl<UserTodoEntity>(emptyList(), pageable, 0)

        every {
            userTodoRepository.findByUserIdOrderByIsCompletedAscCreatedAtDesc(userId, pageable)
        } returns emptyPage

        // When
        val result = useCase.execute(userId)

        // Then
        assertThat(result.content).isEmpty()
        assertThat(result.totalElements).isEqualTo(0)
        verify(exactly = 1) {
            userTodoRepository.findByUserIdOrderByIsCompletedAscCreatedAtDesc(userId, pageable)
        }
    }

    @Test
    @DisplayName("ページサイズを指定してTodoを取得できる")
    fun execute_withCustomPageSize() {
        // Given
        val userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        val pageable = PageRequest.of(0, 10)
        val entities =
            listOf(
                UserTodoEntity(
                    id = 1L,
                    userId = userId,
                    title = "Todo 1",
                    description = "説明 1",
                    isCompleted = false,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                    completedAt = null,
                ),
            )
        val page = PageImpl(entities, pageable, entities.size.toLong())

        every {
            userTodoRepository.findByUserIdOrderByIsCompletedAscCreatedAtDesc(userId, pageable)
        } returns page

        // When
        val result = useCase.execute(userId, page = 0, size = 10)

        // Then
        assertThat(result.content).hasSize(1)
        assertThat(result.size).isEqualTo(10)
        verify(exactly = 1) {
            userTodoRepository.findByUserIdOrderByIsCompletedAscCreatedAtDesc(userId, pageable)
        }
    }

    @Test
    @DisplayName("ページサイズの最大値は100に制限される")
    fun execute_limitsMaxPageSize() {
        // Given
        val userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        val pageable = PageRequest.of(0, 100)
        val emptyPage = PageImpl<UserTodoEntity>(emptyList(), pageable, 0)

        every {
            userTodoRepository.findByUserIdOrderByIsCompletedAscCreatedAtDesc(userId, pageable)
        } returns emptyPage

        // When
        val result = useCase.execute(userId, page = 0, size = 150)

        // Then
        assertThat(result.size).isEqualTo(100)
        verify(exactly = 1) {
            userTodoRepository.findByUserIdOrderByIsCompletedAscCreatedAtDesc(userId, pageable)
        }
    }

    @Test
    @DisplayName("ページサイズの最小値は1に制限される")
    fun execute_limitsMinPageSize() {
        // Given
        val userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        val pageable = PageRequest.of(0, 1)
        val emptyPage = PageImpl<UserTodoEntity>(emptyList(), pageable, 0)

        every {
            userTodoRepository.findByUserIdOrderByIsCompletedAscCreatedAtDesc(userId, pageable)
        } returns emptyPage

        // When
        val result = useCase.execute(userId, page = 0, size = 0)

        // Then
        assertThat(result.size).isEqualTo(1)
        verify(exactly = 1) {
            userTodoRepository.findByUserIdOrderByIsCompletedAscCreatedAtDesc(userId, pageable)
        }
    }

    @Test
    @DisplayName("2ページ目を取得できる")
    fun execute_returnsSecondPage() {
        // Given
        val userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        val pageable = PageRequest.of(1, 20)
        val entities =
            listOf(
                UserTodoEntity(
                    id = 21L,
                    userId = userId,
                    title = "Todo 21",
                    description = "2ページ目の最初",
                    isCompleted = false,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                    completedAt = null,
                ),
            )
        val page = PageImpl(entities, pageable, 25)

        every {
            userTodoRepository.findByUserIdOrderByIsCompletedAscCreatedAtDesc(userId, pageable)
        } returns page

        // When
        val result = useCase.execute(userId, page = 1, size = 20)

        // Then
        assertThat(result.number).isEqualTo(1)
        assertThat(result.totalPages).isEqualTo(2)
        assertThat(result.hasNext()).isFalse
        assertThat(result.hasPrevious()).isTrue
        verify(exactly = 1) {
            userTodoRepository.findByUserIdOrderByIsCompletedAscCreatedAtDesc(userId, pageable)
        }
    }
}
