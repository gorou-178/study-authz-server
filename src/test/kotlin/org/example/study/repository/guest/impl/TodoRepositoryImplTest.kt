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
    @DisplayName("findTodo()はnullではないTodoを返す")
    fun findTodo_returnsNonNullTodo() {
        // When
        val result = todoRepository.findTodo()

        // Then
        assertThat(result).isNotNull
    }

    @Test
    @DisplayName("findTodo()は5件のTodoのいずれかを返す")
    fun findTodo_returnsOneOfTheFiveTodos() {
        // Given
        val allTodos = todoRepository.findAllTodos()

        // When
        val result = todoRepository.findTodo()

        // Then
        assertThat(result).isIn(allTodos)
    }

    @Test
    @DisplayName("findTodo()を複数回呼び出しても毎回Todoを返す")
    fun findTodo_returnsValidTodoMultipleTimes() {
        // When & Then
        repeat(10) {
            val result = todoRepository.findTodo()
            assertThat(result).isNotNull
            assertThat(result).isInstanceOf(Todo::class.java)
        }
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
