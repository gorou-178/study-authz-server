package org.example.study.repository.guest

import org.example.study.domain.model.Todo
import org.example.study.repository.guest.entity.TodoEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TodoRepository : JpaRepository<TodoEntity, Long> {
    fun findAllByCreatedAtAfterOrderByIsCompletedAscCreatedAtDesc(createdAtAfter: LocalDateTime): List<TodoEntity>
}

fun TodoRepository.findTodosWithinLastMonth(): List<Todo> {
    val oneMonthAgo = LocalDateTime.now().minusMonths(1)
    return findAllByCreatedAtAfterOrderByIsCompletedAscCreatedAtDesc(oneMonthAgo).map { it.toDomainModel() }
}
