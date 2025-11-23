package org.example.study.repository.user

import org.example.study.domain.model.Todo
import org.example.study.repository.user.entity.UserTodoEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserTodoRepository : JpaRepository<UserTodoEntity, Long> {
    fun findByUserIdOrderByIsCompletedAscCreatedAtDesc(userId: UUID): List<UserTodoEntity>

    fun findByUserIdOrderByIsCompletedAscCreatedAtDesc(
        userId: UUID,
        pageable: Pageable,
    ): Page<UserTodoEntity>
}

fun UserTodoRepository.findUserTodosSorted(userId: UUID): List<Todo> {
    return findByUserIdOrderByIsCompletedAscCreatedAtDesc(userId).map { it.toDomainModel() }
}

fun UserTodoRepository.findUserTodosSorted(
    userId: UUID,
    pageable: Pageable,
): Page<Todo> {
    return findByUserIdOrderByIsCompletedAscCreatedAtDesc(userId, pageable).map { it.toDomainModel() }
}

fun UserTodoRepository.saveTodo(
    userId: UUID,
    todo: Todo,
): Todo {
    val entity = UserTodoEntity.fromDomainModel(userId, todo)
    val saved = save(entity)
    return saved.toDomainModel()
}
