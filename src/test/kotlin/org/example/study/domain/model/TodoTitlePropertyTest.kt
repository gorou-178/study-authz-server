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

class TodoTitlePropertyTest : StringSpec({

    "有効なタイトルでTodoTitleを作成できる" {
        checkAll(validTodoTitleArb()) { title ->
            val todoTitle = TodoTitle.of(title)
            todoTitle.value shouldBe title
            todoTitle.toString() shouldBe title
        }
    }

    "同じ値を持つTodoTitleインスタンスは等しい" {
        checkAll(validTodoTitleArb()) { title ->
            val todoTitle1 = TodoTitle.of(title)
            val todoTitle2 = TodoTitle.of(title)
            todoTitle1 shouldBe todoTitle2
            todoTitle1.hashCode() shouldBe todoTitle2.hashCode()
        }
    }

    "異なる値を持つTodoTitleインスタンスは等しくない" {
        checkAll(validTodoTitleArb(), validTodoTitleArb()) { title1, title2 ->
            if (title1 != title2) {
                val todoTitle1 = TodoTitle.of(title1)
                val todoTitle2 = TodoTitle.of(title2)
                todoTitle1 shouldNotBe todoTitle2
            }
        }
    }

    "空文字列でTodoTitleを作成するとエラー" {
        val exception =
            shouldThrow<IllegalArgumentException> {
                TodoTitle.of("")
            }
        exception.message shouldContain "1文字以上"
    }

    "101文字以上のタイトルでTodoTitleを作成するとエラー" {
        checkAll(Arb.string(101..200)) { longTitle ->
            val exception =
                shouldThrow<IllegalArgumentException> {
                    TodoTitle.of(longTitle)
                }
            exception.message shouldContain "100文字以内"
        }
    }

    "先頭にスペースがあるタイトルでTodoTitleを作成するとエラー" {
        val titleWithLeadingSpace = " タイトル"
        val exception =
            shouldThrow<IllegalArgumentException> {
                TodoTitle.of(titleWithLeadingSpace)
            }
        exception.message shouldContain "先頭・末尾にスペース"
    }

    "末尾にスペースがあるタイトルでTodoTitleを作成するとエラー" {
        val titleWithTrailingSpace = "タイトル "
        val exception =
            shouldThrow<IllegalArgumentException> {
                TodoTitle.of(titleWithTrailingSpace)
            }
        exception.message shouldContain "先頭・末尾にスペース"
    }

    "半角数字のみのタイトルを作成できる" {
        val title = "123456"
        val todoTitle = TodoTitle.of(title)
        todoTitle.value shouldBe title
    }

    "半角英字(大文字)のみのタイトルを作成できる" {
        val title = "ABCDEFG"
        val todoTitle = TodoTitle.of(title)
        todoTitle.value shouldBe title
    }

    "半角英字(小文字)のみのタイトルを作成できる" {
        val title = "abcdefg"
        val todoTitle = TodoTitle.of(title)
        todoTitle.value shouldBe title
    }

    "ひらがなのみのタイトルを作成できる" {
        val title = "あいうえお"
        val todoTitle = TodoTitle.of(title)
        todoTitle.value shouldBe title
    }

    "カタカナ(全角)のみのタイトルを作成できる" {
        val title = "アイウエオ"
        val todoTitle = TodoTitle.of(title)
        todoTitle.value shouldBe title
    }

    "カタカナ(半角)のみのタイトルを作成できる" {
        val title = "ｱｲｳｴｵ"
        val todoTitle = TodoTitle.of(title)
        todoTitle.value shouldBe title
    }

    "日本語漢字のみのタイトルを作成できる" {
        val title = "日本語"
        val todoTitle = TodoTitle.of(title)
        todoTitle.value shouldBe title
    }

    "記号を含むタイトルを作成できる" {
        val title = "タスク(重要)!"
        val todoTitle = TodoTitle.of(title)
        todoTitle.value shouldBe title
    }

    "スペース(半角)を含むタイトルを作成できる" {
        val title = "Spring Boot学習"
        val todoTitle = TodoTitle.of(title)
        todoTitle.value shouldBe title
    }

    "スペース(全角)を含むタイトルを作成できる" {
        val title = "Spring　Boot　学習"
        val todoTitle = TodoTitle.of(title)
        todoTitle.value shouldBe title
    }

    "混在した文字種のタイトルを作成できる" {
        val title = "Spring Bootの学習123"
        val todoTitle = TodoTitle.of(title)
        todoTitle.value shouldBe title
    }
})

/**
 * 有効なTodoTitle用のArbitrary生成関数
 */
private fun validTodoTitleArb() =
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

        val length = Arb.int(1..100).bind()
        val chars = List(length) { validChars.bind() }
        chars.joinToString("")
    }
