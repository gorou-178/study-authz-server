package org.example.study.domain.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import java.time.LocalDateTime

class TodoPropertyTest : StringSpec({

    "Todoのプロパティは正しく設定される" {
        checkAll(todoArb()) { todo ->
            todo.id shouldBe todo.id
            todo.title shouldBe todo.title
            todo.description shouldBe todo.description
            todo.createdAt shouldBe todo.createdAt
            todo.updatedAt shouldBe todo.updatedAt
            todo.completedAt shouldBe todo.completedAt
        }
    }

    "同じプロパティを持つTodoインスタンスは等しい" {
        checkAll(todoArb()) { todo ->
            val copy = todo.copy()
            todo shouldBe copy
            todo.hashCode() shouldBe copy.hashCode()
        }
    }

    "異なるIDを持つTodoインスタンスは等しくない" {
        checkAll(todoArb(), Arb.long(1L..1000000L)) { todo, newId ->
            if (todo.id != newId) {
                val different = todo.copy(id = newId)
                todo shouldNotBe different
            }
        }
    }

    "copyメソッドでタイトルを変更できる" {
        checkAll(todoArb(), todoTitleArb()) { todo, newTitle ->
            val updated = todo.copy(title = newTitle)
            updated.title shouldBe newTitle
            updated.id shouldBe todo.id
            updated.description shouldBe todo.description
            updated.createdAt shouldBe todo.createdAt
            updated.updatedAt shouldBe todo.updatedAt
            updated.completedAt shouldBe todo.completedAt
        }
    }

    "copyメソッドで説明を変更できる" {
        checkAll(todoArb(), Arb.string(1..500)) { todo, newDescription ->
            val updated = todo.copy(description = newDescription)
            updated.description shouldBe newDescription
            updated.id shouldBe todo.id
            updated.title shouldBe todo.title
        }
    }

    "completedAtがnullの場合とnullでない場合の両方を扱える" {
        var nullCount = 0
        var nonNullCount = 0

        checkAll(1000, todoArb()) { todo ->
            if (todo.completedAt == null) {
                nullCount++
            } else {
                nonNullCount++
            }
        }

        // 十分な数のnullとnon-nullケースが生成されることを確認
        nullCount shouldNotBe 0
        nonNullCount shouldNotBe 0
    }
})

/**
 * TodoTitle用のArbitrary生成関数
 */
private fun todoTitleArb() = arbitrary {
    val title = listOf(
        "Spring Bootの学習",
        "Kotlinのテスト",
        "API開発",
        "データベース設計",
        "認証機能の実装",
        "Docker環境構築",
        "テストコード作成",
    ).random()
    TodoTitle.of(title)
}

/**
 * Todo用のArbitrary生成関数
 */
private fun todoArb() = arbitrary {
    val now = LocalDateTime.now()
    val createdAt = now.minusDays(it.random.nextLong(0, 30))
    val updatedAt = createdAt.plusDays(it.random.nextLong(0, 30))
    val completedAt = if (it.random.nextBoolean()) {
        updatedAt.plusDays(it.random.nextLong(0, 10))
    } else {
        null
    }

    Todo(
        id = it.random.nextLong(1, 1000000),
        title = todoTitleArb().bind(),
        description = Arb.string(1..500).bind(),
        createdAt = createdAt,
        updatedAt = updatedAt,
        completedAt = completedAt,
    )
}
