package org.example.study.controller

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import org.example.study.domain.error.AppError
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

private val logger = LoggerFactory.getLogger("ResultExtensions")

/**
 * Result型をResponseEntityに変換する拡張関数
 * エラーハンドリングを一箇所に集約する
 */
fun <T> Result<T, AppError>.toResponseEntity(): ResponseEntity<*> {
    return fold(
        success = { ResponseEntity.ok(it) },
        failure = { error ->
            when (error) {
                is AppError.ClientError.NotFound ->
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        mapOf(
                            "error" to "Not Found",
                            "message" to "${error.resource} not found: ${error.id}",
                        ),
                    )

                is AppError.ClientError.ValidationError -> {
                    logger.warn("Validation error: ${error.message}", error.cause)
                    ResponseEntity.badRequest().body(
                        mapOf(
                            "error" to "Validation Error",
                            "message" to error.message,
                        ),
                    )
                }

                is AppError.ServerError.DatabaseError -> {
                    logger.error("Database error: ${error.message}", error.cause)
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        mapOf(
                            "error" to "Database Error",
                            "message" to "An error occurred while accessing the database",
                        ),
                    )
                }

                is AppError.ServerError.UnexpectedError -> {
                    logger.error("Unexpected error: ${error.message}", error.cause)
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        mapOf(
                            "error" to "Internal Server Error",
                            "message" to "An unexpected error occurred",
                        ),
                    )
                }
            }
        },
    )
}

/**
 * Result型をResponseEntityに変換する拡張関数（カスタムHTTPステータスコード版）
 */
fun <T> Result<T, AppError>.toResponseEntity(successStatus: HttpStatus): ResponseEntity<*> {
    return fold(
        success = { ResponseEntity.status(successStatus).body(it) },
        failure = { error ->
            // エラーハンドリングは共通のロジックを再利用
            when (error) {
                is AppError.ClientError.NotFound ->
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        mapOf(
                            "error" to "Not Found",
                            "message" to "${error.resource} not found: ${error.id}",
                        ),
                    )

                is AppError.ClientError.ValidationError -> {
                    logger.warn("Validation error: ${error.message}", error.cause)
                    ResponseEntity.badRequest().body(
                        mapOf(
                            "error" to "Validation Error",
                            "message" to error.message,
                        ),
                    )
                }

                is AppError.ServerError.DatabaseError -> {
                    logger.error("Database error: ${error.message}", error.cause)
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        mapOf(
                            "error" to "Database Error",
                            "message" to "An error occurred while accessing the database",
                        ),
                    )
                }

                is AppError.ServerError.UnexpectedError -> {
                    logger.error("Unexpected error: ${error.message}", error.cause)
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        mapOf(
                            "error" to "Internal Server Error",
                            "message" to "An unexpected error occurred",
                        ),
                    )
                }
            }
        },
    )
}
