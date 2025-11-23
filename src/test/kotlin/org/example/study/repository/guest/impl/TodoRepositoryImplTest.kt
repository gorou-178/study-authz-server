package org.example.study.repository.guest.impl

import org.assertj.core.api.Assertions.assertThat
import org.example.study.domain.model.Todo
import org.example.study.domain.model.TodoTitle
import org.example.study.repository.guest.TodoRepository
import org.example.study.repository.guest.findAllTodos
import org.example.study.repository.guest.findTodo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan

@DataJpaTest
@ComponentScan(basePackages = ["org.example.study.repository.guest"])
class TodoRepositoryImplTest {
    @Autowired
    private lateinit var todoRepository: TodoRepository

    @Test
    @DisplayName("findTodo()はソート順で先頭の1件を返す")
    fun findTodo_returnsFirstSortedTodo() {
        // When
        val result = todoRepository.findTodo()
        val allTodos = todoRepository.findAllTodos()

        // Then
        assertThat(result).isNotNull
        assertThat(result).isEqualTo(allTodos.first())
    }

    @Test
    @DisplayName("findTodo()は未完了の最新Todoを返す")
    fun findTodo_returnsLatestIncompleteTodo() {
        // When
        val result = todoRepository.findTodo()

        // Then
        assertThat(result).isNotNull
        // 未完了タスクが優先される
        assertThat(result!!.isCompleted).isFalse
        // ドキュメント作成が最新の未完了タスク
        assertThat(result.title.value).isEqualTo("ドキュメント作成")
    }

    @Test
    @DisplayName("findTodo()を複数回呼び出しても同じTodoを返す")
    fun findTodo_returnsConsistentTodo() {
        // When
        val result1 = todoRepository.findTodo()
        val result2 = todoRepository.findTodo()

        // Then
        assertThat(result1).isNotNull
        assertThat(result2).isNotNull
        assertThat(result1).isEqualTo(result2)
    }

    @Test
    @DisplayName("findAll()は5件のTodoを返す")
    fun findAll_returnsFiveTodos() {
        // When
        val result = todoRepository.findAllTodos()

        // Then
        assertThat(result).hasSize(5)
    }

    @Test
    @DisplayName("findAll()はすべての期待されるTodoを含む")
    fun findAll_containsAllExpectedTodos() {
        // When
        val result = todoRepository.findAllTodos()

        // Then
        assertThat(result).hasSize(5)

        // ID、タイトル、説明の確認
        assertThat(result)
            .extracting("id")
            .containsExactlyInAnyOrder(1L, 2L, 3L, 4L, 5L)

        val titles = result.map { it.title.value }
        assertThat(titles)
            .containsExactlyInAnyOrder(
                "Spring Bootの学習",
                "OAuth2の実装",
                "テストコードの作成",
                "ドキュメント作成",
                "Docker環境構築",
            )
    }

    @Test
    @DisplayName("findAll()は常に同じリストを返す")
    fun findAll_returnsConsistentList() {
        // When
        val result1 = todoRepository.findAllTodos()
        val result2 = todoRepository.findAllTodos()

        // Then
        assertThat(result1).isEqualTo(result2)
    }

    @Test
    @DisplayName("findAll()はisCompleted ASC, createdAt DESCでソートされている")
    fun findAll_returnsSortedByCompletedAndCreatedAt() {
        // When
        val result = todoRepository.findAllTodos()

        // Then
        assertThat(result).hasSize(5)

        // 未完了タスクが先（最新順）
        assertThat(result[0].title.value).isEqualTo("ドキュメント作成") // 未完了、2日前
        assertThat(result[0].isCompleted).isFalse
        assertThat(result[1].title.value).isEqualTo("OAuth2の実装") // 未完了、4日前
        assertThat(result[1].isCompleted).isFalse
        assertThat(result[2].title.value).isEqualTo("Spring Bootの学習") // 未完了、5日前
        assertThat(result[2].isCompleted).isFalse

        // 完了タスクが後（最新順）
        assertThat(result[3].title.value).isEqualTo("テストコードの作成") // 完了、3日前
        assertThat(result[3].isCompleted).isTrue
        assertThat(result[4].title.value).isEqualTo("Docker環境構築") // 完了、6日前
        assertThat(result[4].isCompleted).isTrue
    }

    @Test
    @DisplayName("findAll()で取得したTodoはcompletedAtがnullのものとnullでないものを含む")
    fun findAll_containsTodosWithAndWithoutCompletedAt() {
        // When
        val result = todoRepository.findAllTodos()

        // Then
        val completedTodos = result.filter { it.completedAt != null }
        val incompleteTodos = result.filter { it.completedAt == null }

        assertThat(completedTodos).isNotEmpty
        assertThat(incompleteTodos).isNotEmpty
    }
}
