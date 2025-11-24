package org.example.study.usecase.guest

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.example.study.domain.error.AppError
import org.example.study.domain.model.TodoDescription
import org.example.study.domain.model.TodoTitle
import org.example.study.repository.guest.TodoRepository
import org.example.study.repository.guest.entity.TodoEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CreateTodoUseCaseTest {
    private lateinit var todoRepository: TodoRepository
    private lateinit var useCase: CreateTodoUseCase

    @BeforeEach
    fun setUp() {
        todoRepository = mockk()
        useCase = CreateTodoUseCase(todoRepository)
    }

    @Test
    @DisplayName("正常系: Todoを作成できる")
    fun execute_validInput_returnsTodo() {
        // Given
        val title = "新しいタスク"
        val description = "タスクの説明"
        val savedEntity =
            TodoEntity(
                id = 1L,
                title = title,
                description = description,
                isCompleted = false,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                completedAt = null,
            )

        every { todoRepository.save(any()) } returns savedEntity

        // When
        val result = useCase.execute(title, description)

        // Then
        assertThat(result).isInstanceOf(Ok::class.java)
        val todo = (result as Ok).value
        assertThat(todo.id).isEqualTo(1L)
        assertThat(todo.title).isEqualTo(TodoTitle.of(title))
        assertThat(todo.description).isEqualTo(TodoDescription.of(description))
        assertThat(todo.isCompleted).isFalse

        verify(exactly = 1) { todoRepository.save(any()) }
    }

    @Test
    @DisplayName("正常系: 入力値のトリミングが行われる")
    fun execute_withWhitespace_trimsInput() {
        // Given
        val title = "  タイトル  "
        val description = "  説明  "
        val savedEntity =
            TodoEntity(
                id = 1L,
                title = title.trim(),
                description = description.trim(),
                isCompleted = false,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                completedAt = null,
            )

        every { todoRepository.save(any()) } returns savedEntity

        // When
        val result = useCase.execute(title, description)

        // Then
        assertThat(result).isInstanceOf(Ok::class.java)
        val todo = (result as Ok).value
        assertThat(todo.title.value).isEqualTo("タイトル")
        assertThat(todo.description.value).isEqualTo("説明")
    }

    @Test
    @DisplayName("異常系: タイトルが空の場合はValidationErrorを返す")
    fun execute_emptyTitle_returnsValidationError() {
        // Given
        val title = ""
        val description = "説明"

        // When
        val result = useCase.execute(title, description)

        // Then
        assertThat(result).isInstanceOf(Err::class.java)
        val error = (result as Err).error
        assertThat(error).isInstanceOf(AppError.ClientError.ValidationError::class.java)
        val validationError = error as AppError.ClientError.ValidationError
        assertThat(validationError.message).contains("タイトル")

        verify(exactly = 0) { todoRepository.save(any()) }
    }

    @Test
    @DisplayName("異常系: タイトルが長すぎる場合はValidationErrorを返す")
    fun execute_tooLongTitle_returnsValidationError() {
        // Given
        val title = "a".repeat(256)
        val description = "説明"

        // When
        val result = useCase.execute(title, description)

        // Then
        assertThat(result).isInstanceOf(Err::class.java)
        val error = (result as Err).error
        assertThat(error).isInstanceOf(AppError.ClientError.ValidationError::class.java)

        verify(exactly = 0) { todoRepository.save(any()) }
    }

    @Test
    @DisplayName("異常系: 説明が長すぎる場合はValidationErrorを返す")
    fun execute_tooLongDescription_returnsValidationError() {
        // Given
        val title = "タイトル"
        val description = "a".repeat(1001)

        // When
        val result = useCase.execute(title, description)

        // Then
        assertThat(result).isInstanceOf(Err::class.java)
        val error = (result as Err).error
        assertThat(error).isInstanceOf(AppError.ClientError.ValidationError::class.java)

        verify(exactly = 0) { todoRepository.save(any()) }
    }

    @Test
    @DisplayName("異常系: データベースエラーが発生した場合はDatabaseErrorを返す")
    fun execute_databaseException_returnsDatabaseError() {
        // Given
        val title = "タイトル"
        val description = "説明"
        val dbException = RuntimeException("Database connection failed")

        every { todoRepository.save(any()) } throws dbException

        // When
        val result = useCase.execute(title, description)

        // Then
        assertThat(result).isInstanceOf(Err::class.java)
        val error = (result as Err).error
        assertThat(error).isInstanceOf(AppError.ServerError.DatabaseError::class.java)
        val dbError = error as AppError.ServerError.DatabaseError
        assertThat(dbError.message).isEqualTo("Failed to create todo")
        assertThat(dbError.cause).isEqualTo(dbException)

        verify(exactly = 1) { todoRepository.save(any()) }
    }
}
