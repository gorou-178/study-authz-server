package org.example.study.usecase.user

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.example.study.domain.error.AppError
import org.example.study.domain.model.TodoDescription
import org.example.study.domain.model.TodoTitle
import org.example.study.repository.user.UserTodoRepository
import org.example.study.repository.user.entity.UserTodoEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class CreateUserTodoUseCaseTest {
    private lateinit var userTodoRepository: UserTodoRepository
    private lateinit var useCase: CreateUserTodoUseCase

    @BeforeEach
    fun setUp() {
        userTodoRepository = mockk()
        useCase = CreateUserTodoUseCase(userTodoRepository)
    }

    @Test
    @DisplayName("正常系: ユーザーのTodoを作成できる")
    fun execute_validInput_returnsTodo() {
        // Given
        val userId = UUID.randomUUID()
        val title = "新しいタスク"
        val description = "タスクの説明"
        val savedEntity =
            UserTodoEntity(
                id = 1L,
                userId = userId,
                title = title,
                description = description,
                isCompleted = false,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                completedAt = null,
            )

        every { userTodoRepository.save(any()) } returns savedEntity

        // When
        val result = useCase.execute(userId, title, description)

        // Then
        assertThat(result).isInstanceOf(Ok::class.java)
        val todo = (result as Ok).value
        assertThat(todo.id).isEqualTo(1L)
        assertThat(todo.title).isEqualTo(TodoTitle.of(title))
        assertThat(todo.description).isEqualTo(TodoDescription.of(description))
        assertThat(todo.isCompleted).isFalse

        verify(exactly = 1) { userTodoRepository.save(any()) }
    }

    @Test
    @DisplayName("正常系: 入力値のトリミングが行われる")
    fun execute_withWhitespace_trimsInput() {
        // Given
        val userId = UUID.randomUUID()
        val title = "  タイトル  "
        val description = "  説明  "
        val savedEntity =
            UserTodoEntity(
                id = 1L,
                userId = userId,
                title = title.trim(),
                description = description.trim(),
                isCompleted = false,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                completedAt = null,
            )

        every { userTodoRepository.save(any()) } returns savedEntity

        // When
        val result = useCase.execute(userId, title, description)

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
        val userId = UUID.randomUUID()
        val title = ""
        val description = "説明"

        // When
        val result = useCase.execute(userId, title, description)

        // Then
        assertThat(result).isInstanceOf(Err::class.java)
        val error = (result as Err).error
        assertThat(error).isInstanceOf(AppError.ClientError.ValidationError::class.java)
        val validationError = error as AppError.ClientError.ValidationError
        assertThat(validationError.message).contains("タイトル")

        verify(exactly = 0) { userTodoRepository.save(any()) }
    }

    @Test
    @DisplayName("異常系: タイトルが長すぎる場合はValidationErrorを返す")
    fun execute_tooLongTitle_returnsValidationError() {
        // Given
        val userId = UUID.randomUUID()
        val title = "a".repeat(256)
        val description = "説明"

        // When
        val result = useCase.execute(userId, title, description)

        // Then
        assertThat(result).isInstanceOf(Err::class.java)
        val error = (result as Err).error
        assertThat(error).isInstanceOf(AppError.ClientError.ValidationError::class.java)

        verify(exactly = 0) { userTodoRepository.save(any()) }
    }

    @Test
    @DisplayName("異常系: データベースエラーが発生した場合はDatabaseErrorを返す")
    fun execute_databaseException_returnsDatabaseError() {
        // Given
        val userId = UUID.randomUUID()
        val title = "タイトル"
        val description = "説明"
        val dbException = RuntimeException("Database connection failed")

        every { userTodoRepository.save(any()) } throws dbException

        // When
        val result = useCase.execute(userId, title, description)

        // Then
        assertThat(result).isInstanceOf(Err::class.java)
        val error = (result as Err).error
        assertThat(error).isInstanceOf(AppError.ServerError.DatabaseError::class.java)
        val dbError = error as AppError.ServerError.DatabaseError
        assertThat(dbError.message).isEqualTo("Failed to create user todo")
        assertThat(dbError.cause).isEqualTo(dbException)

        verify(exactly = 1) { userTodoRepository.save(any()) }
    }
}
