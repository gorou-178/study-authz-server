package org.example.study.repository.guest

import org.assertj.core.api.Assertions.assertThat
import org.example.study.repository.guest.entity.TodoEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDateTime

@DataJpaTest
class TodoRepositoryTest {
    @Autowired
    private lateinit var todoRepository: TodoRepository

    @BeforeEach
    fun setUp() {
        todoRepository.deleteAll()
    }

    @Test
    @DisplayName("指定された日時以降のTodoを未完了優先・作成日時降順で取得できる")
    fun findAllByCreatedAtAfterOrderByIsCompletedAscCreatedAtDesc_returnsCorrectOrder() {
        // Given
        val now = LocalDateTime.now()
        val oneWeekAgo = now.minusWeeks(1)
        val twoDaysAgo = now.minusDays(2)
        val yesterday = now.minusDays(1)

        val completedOldTodo =
            TodoEntity(
                title = "完了した古いタスク",
                description = "一週間前に作成",
                isCompleted = true,
                createdAt = oneWeekAgo,
                updatedAt = oneWeekAgo,
                completedAt = oneWeekAgo,
            )
        val incompleteTodo1 =
            TodoEntity(
                title = "未完了タスク1",
                description = "2日前に作成",
                isCompleted = false,
                createdAt = twoDaysAgo,
                updatedAt = twoDaysAgo,
                completedAt = null,
            )
        val incompleteTodo2 =
            TodoEntity(
                title = "未完了タスク2",
                description = "昨日作成",
                isCompleted = false,
                createdAt = yesterday,
                updatedAt = yesterday,
                completedAt = null,
            )

        todoRepository.saveAll(listOf(completedOldTodo, incompleteTodo1, incompleteTodo2))

        // When
        val result =
            todoRepository.findAllByCreatedAtAfterOrderByIsCompletedAscCreatedAtDesc(
                oneWeekAgo.minusDays(1),
            )

        // Then
        assertThat(result).hasSize(3)
        assertThat(result[0].title).isEqualTo("未完了タスク2")
        assertThat(result[0].isCompleted).isFalse
        assertThat(result[1].title).isEqualTo("未完了タスク1")
        assertThat(result[1].isCompleted).isFalse
        assertThat(result[2].title).isEqualTo("完了した古いタスク")
        assertThat(result[2].isCompleted).isTrue
    }

    @Test
    @DisplayName("指定された日時より前のTodoは取得されない")
    fun findAllByCreatedAtAfterOrderByIsCompletedAscCreatedAtDesc_excludesOlderTodos() {
        // Given
        val now = LocalDateTime.now()
        val twoMonthsAgo = now.minusMonths(2)
        val oneWeekAgo = now.minusWeeks(1)

        val oldTodo =
            TodoEntity(
                title = "古いタスク",
                description = "2ヶ月前に作成",
                isCompleted = false,
                createdAt = twoMonthsAgo,
                updatedAt = twoMonthsAgo,
                completedAt = null,
            )
        val recentTodo =
            TodoEntity(
                title = "新しいタスク",
                description = "1週間前に作成",
                isCompleted = false,
                createdAt = oneWeekAgo,
                updatedAt = oneWeekAgo,
                completedAt = null,
            )

        todoRepository.saveAll(listOf(oldTodo, recentTodo))

        // When
        val result =
            todoRepository.findAllByCreatedAtAfterOrderByIsCompletedAscCreatedAtDesc(
                now.minusMonths(1),
            )

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].title).isEqualTo("新しいタスク")
    }

    @Test
    @DisplayName("過去1ヶ月以内のTodoを取得できる")
    fun findTodosWithinLastMonth_returnsRecentTodos() {
        // Given
        val now = LocalDateTime.now()
        val twoMonthsAgo = now.minusMonths(2)
        val twoDaysAgo = now.minusDays(2)

        val oldTodo =
            TodoEntity(
                title = "2ヶ月前のタスク",
                description = "古いタスク",
                isCompleted = false,
                createdAt = twoMonthsAgo,
                updatedAt = twoMonthsAgo,
                completedAt = null,
            )
        val recentTodo =
            TodoEntity(
                title = "最近のタスク",
                description = "2日前のタスク",
                isCompleted = false,
                createdAt = twoDaysAgo,
                updatedAt = twoDaysAgo,
                completedAt = null,
            )

        todoRepository.saveAll(listOf(oldTodo, recentTodo))

        // When
        val result = todoRepository.findTodosWithinLastMonth()

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].title.value).isEqualTo("最近のタスク")
    }

    @Test
    @DisplayName("Todoが無い場合は空のリストを返す")
    fun findTodosWithinLastMonth_returnsEmptyListWhenNoTodos() {
        // When
        val result = todoRepository.findTodosWithinLastMonth()

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    @DisplayName("1ヶ月より古いTodoのみの場合は空のリストを返す")
    fun findTodosWithinLastMonth_returnsEmptyListWhenOnlyOldTodos() {
        // Given
        val twoMonthsAgo = LocalDateTime.now().minusMonths(2)
        val oldTodo =
            TodoEntity(
                title = "古いタスク",
                description = "2ヶ月前のタスク",
                isCompleted = false,
                createdAt = twoMonthsAgo,
                updatedAt = twoMonthsAgo,
                completedAt = null,
            )

        todoRepository.save(oldTodo)

        // When
        val result = todoRepository.findTodosWithinLastMonth()

        // Then
        assertThat(result).isEmpty()
    }
}
