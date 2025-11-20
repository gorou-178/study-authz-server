package org.example.study.repository.user.impl

import org.example.study.domain.model.Todo
import org.example.study.repository.user.UserTodoRepository
import org.example.study.repository.user.entity.UserTodoEntity
import org.example.study.repository.user.jpa.UserTodoJpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class UserTodoRepositoryImpl(
    private val userTodoJpaRepository: UserTodoJpaRepository,
) : UserTodoRepository {
    override fun findByUserId(userId: UUID): List<Todo> {
        return userTodoJpaRepository.findByUserId(userId)
            .map { it.toDomainModel() }
            .sortedWith(
                compareByDescending<Todo> { it.createdAt }
                    .then(nullsLast(compareByDescending { it.completedAt })),
            )
    }

    override fun save(
        userId: UUID,
        todo: Todo,
    ): Todo {
        val entity = UserTodoEntity.fromDomainModel(userId, todo)
        val saved = userTodoJpaRepository.save(entity)
        return saved.toDomainModel()
    }
}
