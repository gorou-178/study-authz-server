package org.example.study.domain.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class TodoDescriptionPropertyTest : StringSpec({

    "有効な説明でTodoDescriptionを作成できる" {
        checkAll(validTodoDescriptionArb()) { description ->
            val todoDescription = TodoDescription.of(description)
            todoDescription.value shouldBe description
            todoDescription.toString() shouldBe description
        }
    }

    "同じ値を持つTodoDescriptionインスタンスは等しい" {
        checkAll(validTodoDescriptionArb()) { description ->
            val todoDescription1 = TodoDescription.of(description)
            val todoDescription2 = TodoDescription.of(description)
            todoDescription1 shouldBe todoDescription2
            todoDescription1.hashCode() shouldBe todoDescription2.hashCode()
        }
    }

    "異なる値を持つTodoDescriptionインスタンスは等しくない" {
        checkAll(validTodoDescriptionArb(), validTodoDescriptionArb()) { description1, description2 ->
            if (description1 != description2) {
                val todoDescription1 = TodoDescription.of(description1)
                val todoDescription2 = TodoDescription.of(description2)
                todoDescription1 shouldNotBe todoDescription2
            }
        }
    }

    "空文字列でTodoDescriptionを作成するとエラー" {
        val exception =
            shouldThrow<IllegalArgumentException> {
                TodoDescription.of("")
            }
        exception.message shouldContain "1文字以上"
    }

    "1001文字以上の説明でTodoDescriptionを作成するとエラー" {
        checkAll(Arb.string(1001..1500)) { longDescription ->
            val exception =
                shouldThrow<IllegalArgumentException> {
                    TodoDescription.of(longDescription)
                }
            exception.message shouldContain "1000文字以内"
        }
    }

    "半角数字のみの説明を作成できる" {
        val description = "123456"
        val todoDescription = TodoDescription.of(description)
        todoDescription.value shouldBe description
    }

    "半角英字(大文字)のみの説明を作成できる" {
        val description = "ABCDEFG"
        val todoDescription = TodoDescription.of(description)
        todoDescription.value shouldBe description
    }

    "半角英字(小文字)のみの説明を作成できる" {
        val description = "abcdefg"
        val todoDescription = TodoDescription.of(description)
        todoDescription.value shouldBe description
    }

    "ひらがなのみの説明を作成できる" {
        val description = "あいうえお"
        val todoDescription = TodoDescription.of(description)
        todoDescription.value shouldBe description
    }

    "カタカナ(全角)のみの説明を作成できる" {
        val description = "アイウエオ"
        val todoDescription = TodoDescription.of(description)
        todoDescription.value shouldBe description
    }

    "カタカナ(半角)のみの説明を作成できる" {
        val description = "ｱｲｳｴｵ"
        val todoDescription = TodoDescription.of(description)
        todoDescription.value shouldBe description
    }

    "日本語漢字のみの説明を作成できる" {
        val description = "日本語"
        val todoDescription = TodoDescription.of(description)
        todoDescription.value shouldBe description
    }

    "記号を含む説明を作成できる" {
        val description = "タスク(重要)!"
        val todoDescription = TodoDescription.of(description)
        todoDescription.value shouldBe description
    }

    "スペース(半角)を含む説明を作成できる" {
        val description = "Spring Boot学習"
        val todoDescription = TodoDescription.of(description)
        todoDescription.value shouldBe description
    }

    "スペース(全角)を含む説明を作成できる" {
        val description = "Spring　Boot　学習"
        val todoDescription = TodoDescription.of(description)
        todoDescription.value shouldBe description
    }

    "改行コード(LF)を含む説明を作成できる" {
        val description = "1行目\n2行目\n3行目"
        val todoDescription = TodoDescription.of(description)
        todoDescription.value shouldBe description
    }

    "改行コード(CR)を含む説明を作成できる" {
        val description = "1行目\r2行目"
        val todoDescription = TodoDescription.of(description)
        todoDescription.value shouldBe description
    }

    "改行コード(CRLF)を含む説明を作成できる" {
        val description = "1行目\r\n2行目\r\n3行目"
        val todoDescription = TodoDescription.of(description)
        todoDescription.value shouldBe description
    }

    "複数行の日本語テキストを含む説明を作成できる" {
        val description =
            """
            Spring Bootの学習を進める

            - REST APIの作成
            - データベース連携
            - 認証機能の実装
            """.trimIndent()
        val todoDescription = TodoDescription.of(description)
        todoDescription.value shouldBe description
    }

    "混在した文字種と改行を含む説明を作成できる" {
        val description = "タイトル: Spring Bootの学習\n内容: Kotlinで実装する123"
        val todoDescription = TodoDescription.of(description)
        todoDescription.value shouldBe description
    }
})

/**
 * 有効なTodoDescription用のArbitrary生成関数
 */
private fun validTodoDescriptionArb() =
    arbitrary {
        // 有効な文字のArbitrary
        val validChars =
            Arb.choice(
                // 半角英数字
                Arb.char('a'..'z'),
                Arb.char('A'..'Z'),
                Arb.char('0'..'9'),
                // ひらがな
                Arb.char('\u3040'..'\u309F'),
                // カタカナ(全角)
                Arb.char('\u30A0'..'\u30FF'),
                // カタカナ(半角)
                Arb.char('\uFF65'..'\uFF9F'),
                // 日本語漢字（一部）
                Arb.choice(
                    Arb.char('\u4E00'..'\u4FFF'),
                    Arb.char('\u5000'..'\u5FFF'),
                    Arb.char('\u6000'..'\u6FFF'),
                    Arb.char('\u7000'..'\u7FFF'),
                    Arb.char('\u8000'..'\u8FFF'),
                    Arb.char('\u9000'..'\u9FAF'),
                ),
            )

        val length = Arb.int(1..500).bind()
        val chars = List(length) { validChars.bind() }
        chars.joinToString("")
    }
