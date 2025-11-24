package org.example.study.extensions

import ch.qos.logback.classic.Level
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import org.assertj.core.api.Assertions.assertThat
import org.example.study.controller.toResponseEntity
import org.example.study.domain.error.AppError
import org.example.study.testutil.LogCapture
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ResultExtensionsTest {
    @Test
    @DisplayName("成功時は200 OKを返す")
    fun toResponseEntity_success_returns200() {
        // Given
        val result = Ok("success data")

        // When
        val response = result.toResponseEntity()

        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo("success data")
    }

    @Test
    @DisplayName("成功時にカスタムステータスコードを返す")
    fun toResponseEntity_successWithCustomStatus_returnsCustomStatus() {
        // Given
        val result = Ok("created data")

        // When
        val response = result.toResponseEntity(HttpStatus.CREATED)

        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isEqualTo("created data")
    }

    @Test
    @DisplayName("NotFoundエラー時は404を返す")
    fun toResponseEntity_notFoundError_returns404() {
        // Given
        val error = AppError.ClientError.NotFound("Todo", 123L)
        val result: com.github.michaelbull.result.Result<String, AppError> = Err(error)

        // When
        val response = result.toResponseEntity()

        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        val body = response.body as Map<*, *>
        assertThat(body["error"]).isEqualTo("Not Found")
        assertThat(body["message"]).isEqualTo("Todo not found: 123")
    }

    @Test
    @DisplayName("ValidationErrorエラー時は400を返し、WARNログを出力する")
    fun toResponseEntity_validationError_returns400AndLogsWarn() {
        // Given
        val error = AppError.ClientError.ValidationError("Invalid input")
        val result: com.github.michaelbull.result.Result<String, AppError> = Err(error)

        // When & Then
        LogCapture.capture("ResultExtensions") {
            val response = result.toResponseEntity()

            // レスポンスの検証
            assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
            val body = response.body as Map<*, *>
            assertThat(body["error"]).isEqualTo("Validation Error")
            assertThat(body["message"]).isEqualTo("Invalid input")

            // ログの検証
            val warnEvents = getWarnEvents()
            assertThat(warnEvents).hasSize(1)
            assertThat(warnEvents[0].formattedMessage).contains("Validation error: Invalid input")
            assertThat(warnEvents[0].level).isEqualTo(Level.WARN)
        }
    }

    @Test
    @DisplayName("DatabaseErrorエラー時は500を返し、ERRORログを出力する")
    fun toResponseEntity_databaseError_returns500AndLogsError() {
        // Given
        val cause = RuntimeException("Connection timeout")
        val error = AppError.ServerError.DatabaseError("Failed to save data", cause)
        val result: com.github.michaelbull.result.Result<String, AppError> = Err(error)

        // When & Then
        LogCapture.capture("ResultExtensions") {
            val response = result.toResponseEntity()

            // レスポンスの検証
            assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
            val body = response.body as Map<*, *>
            assertThat(body["error"]).isEqualTo("Database Error")
            assertThat(body["message"]).isEqualTo("An error occurred while accessing the database")

            // ログの検証
            val errorEvents = getErrorEvents()
            assertThat(errorEvents).hasSize(1)
            assertThat(errorEvents[0].formattedMessage).contains("Database error: Failed to save data")
            assertThat(errorEvents[0].level).isEqualTo(Level.ERROR)
            assertThat(errorEvents[0].throwableProxy).isNotNull
            assertThat(errorEvents[0].throwableProxy.className).isEqualTo("java.lang.RuntimeException")
        }
    }

    @Test
    @DisplayName("UnexpectedErrorエラー時は500を返し、ERRORログを出力する")
    fun toResponseEntity_unexpectedError_returns500AndLogsError() {
        // Given
        val cause = IllegalStateException("Unexpected state")
        val error = AppError.ServerError.UnexpectedError("Something went wrong", cause)
        val result: com.github.michaelbull.result.Result<String, AppError> = Err(error)

        // When & Then
        LogCapture.capture("ResultExtensions") {
            val response = result.toResponseEntity()

            // レスポンスの検証
            assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
            val body = response.body as Map<*, *>
            assertThat(body["error"]).isEqualTo("Internal Server Error")
            assertThat(body["message"]).isEqualTo("An unexpected error occurred")

            // ログの検証
            val errorEvents = getErrorEvents()
            assertThat(errorEvents).hasSize(1)
            assertThat(errorEvents[0].formattedMessage).contains("Unexpected error: Something went wrong")
            assertThat(errorEvents[0].level).isEqualTo(Level.ERROR)
            assertThat(errorEvents[0].throwableProxy).isNotNull
        }
    }

    @Test
    @DisplayName("カスタムステータスコード指定時もエラーログを正しく出力する")
    fun toResponseEntity_withCustomStatusAndDatabaseError_logsError() {
        // Given
        val error = AppError.ServerError.DatabaseError("Save failed", null)
        val result: com.github.michaelbull.result.Result<String, AppError> = Err(error)

        // When & Then
        LogCapture.capture("ResultExtensions") {
            val response = result.toResponseEntity(HttpStatus.CREATED)

            // レスポンスの検証（エラー時はステータスコードは500）
            assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)

            // ログの検証
            val errorEvents = getErrorEvents()
            assertThat(errorEvents).hasSize(1)
            assertThat(errorEvents[0].level).isEqualTo(Level.ERROR)
        }
    }
}
