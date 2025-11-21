package org.example.study.repository.guest

import org.example.study.domain.model.Todo
import org.example.study.repository.guest.entity.TodoEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository : JpaRepository<TodoEntity, Long> {
    @Query(value = "SELECT * FROM todos ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    fun findRandomTodoEntity(): TodoEntity?

    fun findAllByOrderByIsCompletedAscCreatedAtDesc(): List<TodoEntity>
}

fun TodoRepository.findTodo(): Todo? {
    return findRandomTodoEntity()?.toDomainModel()
}

fun TodoRepository.findAllTodos(): List<Todo> {
    return findAllByOrderByIsCompletedAscCreatedAtDesc().map { it.toDomainModel() }
}
