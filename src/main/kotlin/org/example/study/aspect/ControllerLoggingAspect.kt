package org.example.study.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/**
 * Controllerの処理開始・終了を自動的にログ出力するAspect
 */
@Aspect
@Component
class ControllerLoggingAspect {
    private val logger = LoggerFactory.getLogger(ControllerLoggingAspect::class.java)

    /**
     * org.example.study.controller配下の全てのpublicメソッドに対して
     * 処理開始・終了のログを出力する
     */
    @Around("execution(public * org.example.study.controller..*.*(..))")
    fun logAround(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as MethodSignature
        val className = signature.declaringType.simpleName
        val methodName = signature.name

        // リクエスト情報の取得
        val requestInfo = getRequestInfo()

        // 処理開始ログ
        logger.info("[Controller] START: {}.{}() {}", className, methodName, requestInfo)

        val startTime = System.currentTimeMillis()
        return try {
            // 実際の処理を実行
            val result = joinPoint.proceed()

            // 実行時間を計算
            val executionTime = System.currentTimeMillis() - startTime

            // 処理終了ログ（成功）
            logger.info(
                "[Controller] END: {}.{}() - {}ms {}",
                className,
                methodName,
                executionTime,
                requestInfo,
            )

            result
        } catch (e: Exception) {
            // 実行時間を計算
            val executionTime = System.currentTimeMillis() - startTime

            // 処理終了ログ（エラー）
            logger.info(
                "[Controller] END: {}.{}() - {}ms {} [ERROR: {}]",
                className,
                methodName,
                executionTime,
                requestInfo,
                e.javaClass.simpleName,
            )

            // 例外を再スロー
            throw e
        }
    }

    /**
     * HTTPリクエスト情報を取得
     */
    private fun getRequestInfo(): String {
        return try {
            val attributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
            val request = attributes?.request

            if (request != null) {
                val method = request.method
                val uri = request.requestURI
                val queryString = request.queryString?.let { "?$it" } ?: ""
                "[HTTP $method $uri$queryString]"
            } else {
                ""
            }
        } catch (e: Exception) {
            // リクエストコンテキストが取得できない場合（テスト時など）
            ""
        }
    }
}
