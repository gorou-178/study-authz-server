package org.example.study.repository.user

import org.example.study.domain.model.Todo
import org.example.study.repository.user.entity.UserTodoEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserTodoRepository : JpaRepository<UserTodoEntity, Long> {
    @Query(
        """
        SELECT u FROM UserTodoEntity u
        WHERE u.userId = :userId
        ORDER BY u.createdAt DESC,
                 CASE WHEN u.completedAt IS NULL THEN 1 ELSE 0 END,
                 u.completedAt DESC
        """,
    )
    fun findByUserIdSorted(
        @Param("userId") userId: UUID,
    ): List<UserTodoEntity>
}

fun UserTodoRepository.findUserTodosSorted(userId: UUID): List<Todo> {
    return findByUserIdSorted(userId).map { it.toDomainModel() }
}

fun UserTodoRepository.saveTodo(
    userId: UUID,
    todo: Todo,
): Todo {
    val entity = UserTodoEntity.fromDomainModel(userId, todo)
    val saved = save(entity)
    return saved.toDomainModel()
}
