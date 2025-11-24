package org.example.study.usecase.guest

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.example.study.domain.error.AppError
import org.example.study.repository.guest.TodoRepository
import org.example.study.repository.guest.entity.TodoEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class GetTodoUseCaseTest {
    private lateinit var todoRepository: TodoRepository
    private lateinit var useCase: GetTodoUseCase

    @BeforeEach
    fun setUp() {
        todoRepository = mockk()
        useCase = GetTodoUseCase(todoRepository)
    }

    @Test
    @DisplayName("正常系: Todoのリストを取得できる")
    fun execute_withTodos_returnsTodoList() {
        // Given
        val entities =
            listOf(
                TodoEntity(
                    id = 1L,
                    title = "タスク1",
                    description = "説明1",
                    isCompleted = false,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                    completedAt = null,
                ),
                TodoEntity(
                    id = 2L,
                    title = "タスク2",
                    description = "説明2",
                    isCompleted = false,
                    createdAt = LocalDateTime.now().minusDays(1),
                    updatedAt = LocalDateTime.now().minusDays(1),
                    completedAt = null,
                ),
            )

        every {
            todoRepository.findAllByCreatedAtAfterOrderByIsCompletedAscCreatedAtDesc(any())
        } returns entities

        // When
        val result = useCase.execute()

        // Then
        assertThat(result).isInstanceOf(Ok::class.java)
        val todos = (result as Ok).value
        assertThat(todos).hasSize(2)
        assertThat(todos[0].id).isEqualTo(1L)
        assertThat(todos[1].id).isEqualTo(2L)

        verify(exactly = 1) {
            todoRepository.findAllByCreatedAtAfterOrderByIsCompletedAscCreatedAtDesc(any())
        }
    }

    @Test
    @DisplayName("正常系: Todoが存在しない場合は空のリストを返す")
    fun execute_withNoTodos_returnsEmptyList() {
        // Given
        every {
            todoRepository.findAllByCreatedAtAfterOrderByIsCompletedAscCreatedAtDesc(any())
        } returns emptyList()

        // When
        val result = useCase.execute()

        // Then
        assertThat(result).isInstanceOf(Ok::class.java)
        val todos = (result as Ok).value
        assertThat(todos).isEmpty()

        verify(exactly = 1) {
            todoRepository.findAllByCreatedAtAfterOrderByIsCompletedAscCreatedAtDesc(any())
        }
    }

    @Test
    @DisplayName("異常系: データベースエラーが発生した場合はDatabaseErrorを返す")
    fun execute_databaseException_returnsDatabaseError() {
        // Given
        val dbException = RuntimeException("Database connection failed")

        every {
            todoRepository.findAllByCreatedAtAfterOrderByIsCompletedAscCreatedAtDesc(any())
        } throws dbException

        // When
        val result = useCase.execute()

        // Then
        assertThat(result).isInstanceOf(Err::class.java)
        val error = (result as Err).error
        assertThat(error).isInstanceOf(AppError.ServerError.DatabaseError::class.java)
        val dbError = error as AppError.ServerError.DatabaseError
        assertThat(dbError.message).isEqualTo("Failed to fetch todos")
        assertThat(dbError.cause).isEqualTo(dbException)

        verify(exactly = 1) {
            todoRepository.findAllByCreatedAtAfterOrderByIsCompletedAscCreatedAtDesc(any())
        }
    }
}
