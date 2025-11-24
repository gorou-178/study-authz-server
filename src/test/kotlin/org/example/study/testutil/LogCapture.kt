package org.example.study.testutil

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.slf4j.LoggerFactory

/**
 * ログをキャプチャしてテストするためのユーティリティクラス
 */
class LogCapture(loggerName: String) : AutoCloseable {
    private val logger: Logger = LoggerFactory.getLogger(loggerName) as Logger
    private val listAppender: ListAppender<ILoggingEvent> = ListAppender()

    init {
        listAppender.start()
        logger.addAppender(listAppender)
    }

    /**
     * キャプチャしたログイベントのリスト
     */
    val events: List<ILoggingEvent>
        get() = listAppender.list

    /**
     * 指定したレベルのログイベントを取得
     */
    fun getEvents(level: Level): List<ILoggingEvent> = events.filter { it.level == level }

    /**
     * ERRORレベルのログイベントを取得
     */
    fun getErrorEvents(): List<ILoggingEvent> = getEvents(Level.ERROR)

    /**
     * WARNレベルのログイベントを取得
     */
    fun getWarnEvents(): List<ILoggingEvent> = getEvents(Level.WARN)

    /**
     * 指定したメッセージを含むログイベントが存在するかチェック
     */
    fun containsMessage(
        message: String,
        level: Level? = null,
    ): Boolean {
        val targetEvents = level?.let { getEvents(it) } ?: events
        return targetEvents.any { it.formattedMessage.contains(message) }
    }

    /**
     * 指定したレベルのログが指定された回数出力されたかチェック
     */
    fun assertLogCount(
        level: Level,
        expectedCount: Int,
    ) {
        val actualCount = getEvents(level).size
        require(actualCount == expectedCount) {
            "Expected $expectedCount $level logs, but found $actualCount"
        }
    }

    /**
     * リソースのクリーンアップ
     */
    override fun close() {
        logger.detachAppender(listAppender)
        listAppender.stop()
    }

    companion object {
        /**
         * ログキャプチャを実行するヘルパー関数
         */
        inline fun <T> capture(
            loggerName: String,
            block: LogCapture.() -> T,
        ): T {
            return LogCapture(loggerName).use(block)
        }
    }
}
