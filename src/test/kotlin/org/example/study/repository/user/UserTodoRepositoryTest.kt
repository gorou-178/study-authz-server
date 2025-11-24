package org.example.study.repository.user

import org.assertj.core.api.Assertions.assertThat
import org.example.study.domain.model.Todo
import org.example.study.domain.model.TodoDescription
import org.example.study.domain.model.TodoTitle
import org.example.study.repository.user.entity.UserTodoEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import java.util.UUID

@DataJpaTest
class UserTodoRepositoryTest {
    @Autowired
    private lateinit var userTodoRepository: UserTodoRepository

    private val testUserId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
    private val anotherUserId = UUID.fromString("00000000-0000-0000-0000-000000000001")

    @BeforeEach
    fun setUp() {
        userTodoRepository.deleteAll()
    }

    @Test
    @DisplayName("指定されたユーザーのTodoを未完了優先・作成日時降順で取得できる")
    fun findByUserIdOrderByIsCompletedAscCreatedAtDesc_returnsCorrectOrder() {
        // Given
        val now = LocalDateTime.now()
        val yesterday = now.minusDays(1)
        val twoDaysAgo = now.minusDays(2)

        val completedTodo =
            UserTodoEntity(
                userId = testUserId,
                title = "完了タスク",
                description = "完了済み",
                isCompleted = true,
                createdAt = twoDaysAgo,
                updatedAt = twoDaysAgo,
                completedAt = twoDaysAgo,
            )
        val incompleteTodo1 =
            UserTodoEntity(
                userId = testUserId,
                title = "未完了タスク1",
                description = "昨日作成",
                isCompleted = false,
                createdAt = yesterday,
                updatedAt = yesterday,
                completedAt = null,
            )
        val incompleteTodo2 =
            UserTodoEntity(
                userId = testUserId,
                title = "未完了タスク2",
                description = "今日作成",
                isCompleted = false,
                createdAt = now,
                updatedAt = now,
                completedAt = null,
            )

        userTodoRepository.saveAll(listOf(completedTodo, incompleteTodo1, incompleteTodo2))

        // When
        val pageable = PageRequest.of(0, 10)
        val result = userTodoRepository.findByUserIdOrderByIsCompletedAscCreatedAtDesc(testUserId, pageable)

        // Then
        assertThat(result.content).hasSize(3)
        assertThat(result.content[0].title).isEqualTo("未完了タスク2")
        assertThat(result.content[0].isCompleted).isFalse
        assertThat(result.content[1].title).isEqualTo("未完了タスク1")
        assertThat(result.content[1].isCompleted).isFalse
        assertThat(result.content[2].title).isEqualTo("完了タスク")
        assertThat(result.content[2].isCompleted).isTrue
    }

    @Test
    @DisplayName("指定されたユーザーのTodoのみを取得する")
    fun findByUserIdOrderByIsCompletedAscCreatedAtDesc_filtersCorrectUser() {
        // Given
        val now = LocalDateTime.now()
        val userTodo =
            UserTodoEntity(
                userId = testUserId,
                title = "ユーザー1のタスク",
                description = "テストユーザー",
                isCompleted = false,
                createdAt = now,
                updatedAt = now,
                completedAt = null,
            )
        val anotherUserTodo =
            UserTodoEntity(
                userId = anotherUserId,
                title = "ユーザー2のタスク",
                description = "別のユーザー",
                isCompleted = false,
                createdAt = now,
                updatedAt = now,
                completedAt = null,
            )

        userTodoRepository.saveAll(listOf(userTodo, anotherUserTodo))

        // When
        val pageable = PageRequest.of(0, 10)
        val result = userTodoRepository.findByUserIdOrderByIsCompletedAscCreatedAtDesc(testUserId, pageable)

        // Then
        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].title).isEqualTo("ユーザー1のタスク")
        assertThat(result.content[0].userId).isEqualTo(testUserId)
    }

    @Test
    @DisplayName("ページネーションが正しく機能する")
    fun findByUserIdOrderByIsCompletedAscCreatedAtDesc_paginationWorks() {
        // Given
        val now = LocalDateTime.now()
        val todos =
            (1..25).map { i ->
                UserTodoEntity(
                    userId = testUserId,
                    title = "タスク$i",
                    description = "説明$i",
                    isCompleted = false,
                    createdAt = now.minusDays(i.toLong()),
                    updatedAt = now.minusDays(i.toLong()),
                    completedAt = null,
                )
            }
        userTodoRepository.saveAll(todos)

        // When - 1ページ目
        val page0 =
            userTodoRepository.findByUserIdOrderByIsCompletedAscCreatedAtDesc(
                testUserId,
                PageRequest.of(0, 10),
            )

        // Then
        assertThat(page0.content).hasSize(10)
        assertThat(page0.totalElements).isEqualTo(25)
        assertThat(page0.totalPages).isEqualTo(3)
        assertThat(page0.number).isEqualTo(0)
        assertThat(page0.hasNext()).isTrue
        assertThat(page0.hasPrevious()).isFalse

        // When - 2ページ目
        val page1 =
            userTodoRepository.findByUserIdOrderByIsCompletedAscCreatedAtDesc(
                testUserId,
                PageRequest.of(1, 10),
            )

        // Then
        assertThat(page1.content).hasSize(10)
        assertThat(page1.number).isEqualTo(1)
        assertThat(page1.hasNext()).isTrue
        assertThat(page1.hasPrevious()).isTrue

        // When - 3ページ目（最後のページ）
        val page2 =
            userTodoRepository.findByUserIdOrderByIsCompletedAscCreatedAtDesc(
                testUserId,
                PageRequest.of(2, 10),
            )

        // Then
        assertThat(page2.content).hasSize(5)
        assertThat(page2.number).isEqualTo(2)
        assertThat(page2.hasNext()).isFalse
        assertThat(page2.hasPrevious()).isTrue
    }

    @Test
    @DisplayName("ユーザーにTodoが無い場合は空のページを返す")
    fun findByUserIdOrderByIsCompletedAscCreatedAtDesc_returnsEmptyPageWhenNoTodos() {
        // When
        val pageable = PageRequest.of(0, 10)
        val result = userTodoRepository.findByUserIdOrderByIsCompletedAscCreatedAtDesc(testUserId, pageable)

        // Then
        assertThat(result.content).isEmpty()
        assertThat(result.totalElements).isEqualTo(0)
        assertThat(result.totalPages).isEqualTo(0)
    }

    @Test
    @DisplayName("saveTodo拡張関数で新しいTodoを保存できる")
    fun saveTodo_savesNewTodo() {
        // Given
        val now = LocalDateTime.now()
        val newTodo =
            Todo(
                id = 0L,
                title = TodoTitle.of("新しいタスク"),
                description = TodoDescription.of("テスト用のタスク"),
                isCompleted = false,
                createdAt = now,
                updatedAt = now,
                completedAt = null,
            )

        // When
        val savedTodo = userTodoRepository.saveTodo(testUserId, newTodo)

        // Then
        assertThat(savedTodo.id).isNotEqualTo(0L)
        assertThat(savedTodo.title.value).isEqualTo("新しいタスク")
        assertThat(savedTodo.description.value).isEqualTo("テスト用のタスク")
        assertThat(savedTodo.isCompleted).isFalse

        val saved = userTodoRepository.findById(savedTodo.id).get()
        assertThat(saved.userId).isEqualTo(testUserId)
        assertThat(saved.title).isEqualTo("新しいタスク")
    }

    @Test
    @DisplayName("saveTodo拡張関数で既存のTodoを更新できる")
    fun saveTodo_updatesExistingTodo() {
        // Given
        val now = LocalDateTime.now()
        val entity =
            UserTodoEntity(
                userId = testUserId,
                title = "元のタイトル",
                description = "元の説明",
                isCompleted = false,
                createdAt = now,
                updatedAt = now,
                completedAt = null,
            )
        val savedEntity = userTodoRepository.save(entity)

        val updatedTodo =
            Todo(
                id = savedEntity.id!!,
                title = TodoTitle.of("更新後のタイトル"),
                description = TodoDescription.of("更新後の説明"),
                isCompleted = true,
                createdAt = now,
                updatedAt = now.plusHours(1),
                completedAt = now.plusHours(1),
            )

        // When
        val updatedResult = userTodoRepository.saveTodo(testUserId, updatedTodo)

        // Then
        assertThat(updatedResult.id).isEqualTo(savedEntity.id)
        assertThat(updatedResult.title.value).isEqualTo("更新後のタイトル")
        assertThat(updatedResult.description.value).isEqualTo("更新後の説明")
        assertThat(updatedResult.isCompleted).isTrue
        assertThat(updatedResult.completedAt).isNotNull

        val updated = userTodoRepository.findById(savedEntity.id!!).get()
        assertThat(updated.title).isEqualTo("更新後のタイトル")
        assertThat(updated.isCompleted).isTrue
    }

    @Test
    @DisplayName("saveTodo拡張関数でid=0の新規Todoが自動採番される")
    fun saveTodo_autoGeneratesIdForNewTodo() {
        // Given
        val now = LocalDateTime.now()
        val newTodo =
            Todo(
                id = 0L,
                title = TodoTitle.of("新規タスク"),
                description = TodoDescription.of("ID自動採番テスト"),
                isCompleted = false,
                createdAt = now,
                updatedAt = now,
                completedAt = null,
            )

        // When
        val savedTodo = userTodoRepository.saveTodo(testUserId, newTodo)

        // Then
        assertThat(savedTodo.id).isGreaterThan(0L)

        val all = userTodoRepository.findAll()
        assertThat(all).hasSize(1)
        assertThat(all[0].id).isEqualTo(savedTodo.id)
    }
}
