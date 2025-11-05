package org.example.study.repository.guest.jpa

import org.example.study.repository.guest.entity.TodoEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TodoJpaRepository : JpaRepository<TodoEntity, Long> {
    @Query(value = "SELECT * FROM todos ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    fun findRandomTodo(): TodoEntity?
}
