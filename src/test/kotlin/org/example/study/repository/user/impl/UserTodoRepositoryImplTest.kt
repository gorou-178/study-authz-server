package org.example.study.repository.user.impl

import org.assertj.core.api.Assertions.assertThat
import org.example.study.domain.model.Todo
import org.example.study.domain.model.TodoDescription
import org.example.study.domain.model.TodoTitle
import org.example.study.repository.user.entity.UserTodoEntity
import org.example.study.repository.user.jpa.UserTodoJpaRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import java.time.LocalDateTime
import java.util.UUID

@DataJpaTest
@ComponentScan(basePackages = ["org.example.study.repository.user"])
class UserTodoRepositoryImplTest {
    @Autowired
    private lateinit var userTodoRepository: UserTodoRepositoryImpl

    @Autowired
    private lateinit var userTodoJpaRepository: UserTodoJpaRepository

    private val testUserId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")

    @BeforeEach
    fun setUp() {
        userTodoJpaRepository.deleteAll()
    }

    @Test
    @DisplayName("findByUserId()はcreatedAtの降順でソートされたTodoリストを返す")
    fun findByUserId_returnsSortedByCreatedAtDescending() {
        // Given
        val now = LocalDateTime.now()
        val todo1 =
            UserTodoEntity(
                userId = testUserId,
                title = "Todo 1",
                description = "最も古い",
                createdAt = now.minusDays(3),
                updatedAt = now.minusDays(3),
                completedAt = null,
            )
        val todo2 =
            UserTodoEntity(
                userId = testUserId,
                title = "Todo 2",
                description = "中間",
                createdAt = now.minusDays(2),
                updatedAt = now.minusDays(2),
                completedAt = null,
            )
        val todo3 =
            UserTodoEntity(
                userId = testUserId,
                title = "Todo 3",
                description = "最新",
                createdAt = now.minusDays(1),
                updatedAt = now.minusDays(1),
                completedAt = null,
            )

        userTodoJpaRepository.saveAll(listOf(todo1, todo2, todo3))

        // When
        val result = userTodoRepository.findByUserId(testUserId)

        // Then
        assertThat(result).hasSize(3)
        assertThat(result.map { it.title.value })
            .containsExactly("Todo 3", "Todo 2", "Todo 1")
    }

    @Test
    @DisplayName("findByUserId()は同じcreatedAtの場合、completedAtの降順でソートされる")
    fun findByUserId_returnsSortedByCompletedAtDescendingWhenCreatedAtIsSame() {
        // Given
        val now = LocalDateTime.now()
        val sameCreatedAt = now.minusDays(1)

        val todo1 =
            UserTodoEntity(
                userId = testUserId,
                title = "Todo 1",
                description = "completedAt = null",
                createdAt = sameCreatedAt,
                updatedAt = sameCreatedAt,
                completedAt = null,
            )
        val todo2 =
            UserTodoEntity(
                userId = testUserId,
                title = "Todo 2",
                description = "completedAt = 2日前",
                createdAt = sameCreatedAt,
                updatedAt = sameCreatedAt,
                completedAt = now.minusDays(2),
            )
        val todo3 =
            UserTodoEntity(
                userId = testUserId,
                title = "Todo 3",
                description = "completedAt = 1日前",
                createdAt = sameCreatedAt,
                updatedAt = sameCreatedAt,
                completedAt = now.minusDays(1),
            )

        userTodoJpaRepository.saveAll(listOf(todo1, todo2, todo3))

        // When
        val result = userTodoRepository.findByUserId(testUserId)

        // Then
        assertThat(result).hasSize(3)
        // completedAtの降順（nullは最後）: 1日前 -> 2日前 -> null
        assertThat(result.map { it.title.value })
            .containsExactly("Todo 3", "Todo 2", "Todo 1")
    }

    @Test
    @DisplayName("findByUserId()は複雑なソート条件を正しく処理する")
    fun findByUserId_handlesComplexSortConditionsCorrectly() {
        // Given
        val now = LocalDateTime.now()

        val todo1 =
            UserTodoEntity(
                userId = testUserId,
                title = "Todo 1",
                description = "最新のcreatedAt completedAt = null",
                createdAt = now.minusDays(1),
                updatedAt = now,
                completedAt = null,
            )
        val todo2 =
            UserTodoEntity(
                userId = testUserId,
                title = "Todo 2",
                description = "最新のcreatedAt completedAt = 1日前",
                createdAt = now.minusDays(1),
                updatedAt = now,
                completedAt = now.minusDays(1),
            )
        val todo3 =
            UserTodoEntity(
                userId = testUserId,
                title = "Todo 3",
                description = "古いcreatedAt completedAt = null",
                createdAt = now.minusDays(3),
                updatedAt = now,
                completedAt = null,
            )
        val todo4 =
            UserTodoEntity(
                userId = testUserId,
                title = "Todo 4",
                description = "古いcreatedAt completedAt = 2日前",
                createdAt = now.minusDays(3),
                updatedAt = now,
                completedAt = now.minusDays(2),
            )

        userTodoJpaRepository.saveAll(listOf(todo1, todo2, todo3, todo4))

        // When
        val result = userTodoRepository.findByUserId(testUserId)

        // Then
        assertThat(result).hasSize(4)
        // 期待される順序:
        // 1. Todo 2 (createdAt: 1日前, completedAt: 1日前)
        // 2. Todo 1 (createdAt: 1日前, completedAt: null)
        // 3. Todo 4 (createdAt: 3日前, completedAt: 2日前)
        // 4. Todo 3 (createdAt: 3日前, completedAt: null)
        assertThat(result.map { it.title.value })
            .containsExactly("Todo 2", "Todo 1", "Todo 4", "Todo 3")
    }

    @Test
    @DisplayName("findByUserId()は空のリストを返す場合がある")
    fun findByUserId_returnsEmptyListWhenNoTodosExist() {
        // When
        val result = userTodoRepository.findByUserId(testUserId)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    @DisplayName("findByUserId()は指定されたユーザーIDのTodoのみを返す")
    fun findByUserId_returnsOnlyTodosForSpecifiedUserId() {
        // Given
        val now = LocalDateTime.now()
        val otherUserId = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8")

        val todo1 =
            UserTodoEntity(
                userId = testUserId,
                title = "Todo for test user",
                description = "テストユーザーのTodo",
                createdAt = now,
                updatedAt = now,
                completedAt = null,
            )
        val todo2 =
            UserTodoEntity(
                userId = otherUserId,
                title = "Todo for other user",
                description = "他のユーザーのTodo",
                createdAt = now,
                updatedAt = now,
                completedAt = null,
            )

        userTodoJpaRepository.saveAll(listOf(todo1, todo2))

        // When
        val result = userTodoRepository.findByUserId(testUserId)

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].title.value).isEqualTo("Todo for test user")
    }

    @Test
    @DisplayName("save()は新しいTodoを保存できる")
    fun save_savesNewTodo() {
        // Given
        val todo =
            Todo(
                id = 0L,
                title = TodoTitle.of("新しいTodo"),
                description = TodoDescription.of("説明"),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                completedAt = null,
            )

        // When
        val saved = userTodoRepository.save(testUserId, todo)

        // Then
        assertThat(saved.id).isNotEqualTo(0L)
        assertThat(saved.title.value).isEqualTo("新しいTodo")
        assertThat(saved.description.value).isEqualTo("説明")

        val found = userTodoRepository.findByUserId(testUserId)
        assertThat(found).hasSize(1)
        assertThat(found[0].title.value).isEqualTo("新しいTodo")
    }
}
