package org.example.study.domain.error

/**
 * アプリケーション全体で使用するエラー型
 */
sealed class AppError {
    /**
     * クライアントエラー（4xx系）
     */
    sealed class ClientError : AppError() {
        /**
         * リソースが見つからない
         */
        data class NotFound(val resource: String, val id: Any) : ClientError()

        /**
         * バリデーションエラー
         */
        data class ValidationError(val message: String, val cause: Throwable? = null) : ClientError()
    }

    /**
     * サーバーエラー（5xx系）
     */
    sealed class ServerError : AppError() {
        /**
         * データベースエラー
         */
        data class DatabaseError(val message: String, val cause: Throwable? = null) : ServerError()

        /**
         * 予期しないエラー
         */
        data class UnexpectedError(val message: String, val cause: Throwable? = null) : ServerError()
    }
}
