package org.example.study.domain.model

/**
 * Todoのタイトルを表すValue Object
 *
 * 許可される文字:
 * - 半角数字: 0-9
 * - 半角英字(大文字): A-Z
 * - 半角英字(小文字): a-z
 * - 日本語漢字
 * - カタカナ(全角)
 * - カタカナ(半角)
 * - ひらがな
 * - スペース(半角/全角) ※先頭・末尾はNG
 * - 記号
 */
data class TodoTitle private constructor(val value: String) {
    companion object {
        private const val MAX_LENGTH = 100
        private const val MIN_LENGTH = 1

        // 許可される文字のパターン
        // - 半角英数字: a-zA-Z0-9
        // - ひらがな: \u3040-\u309F
        // - カタカナ(全角): \u30A0-\u30FF
        // - カタカナ(半角): \uFF65-\uFF9F
        // - 日本語漢字: \u4E00-\u9FAF
        // - スペース: 半角と全角
        // - 記号: 一般的な記号を含む
        private val ALLOWED_CHARACTERS_PATTERN =
            Regex("^[a-zA-Z0-9\\u3040-\\u309F\\u30A0-\\u30FF\\uFF65-\\uFF9F\\u4E00-\\u9FAF !\"#\$%&'()*+,\\-./:;<=>?@\\[\\]^_`{|}~　]+$")

        /**
         * TodoTitleを生成する
         *
         * @param value タイトル文字列
         * @return TodoTitle
         * @throws IllegalArgumentException バリデーションエラー時
         */
        fun of(value: String): TodoTitle {
            require(value.length >= MIN_LENGTH) {
                "タイトルは${MIN_LENGTH}文字以上である必要があります"
            }
            require(value.length <= MAX_LENGTH) {
                "タイトルは${MAX_LENGTH}文字以内である必要があります"
            }
            require(value.trim() == value) {
                "タイトルの先頭・末尾にスペースを含めることはできません"
            }
            require(ALLOWED_CHARACTERS_PATTERN.matches(value)) {
                "タイトルに使用できない文字が含まれています"
            }

            return TodoTitle(value)
        }
    }

    override fun toString(): String = value
}
