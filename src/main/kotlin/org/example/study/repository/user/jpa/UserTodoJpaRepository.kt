package org.example.study.repository.user.jpa

import org.example.study.repository.user.entity.UserTodoEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserTodoJpaRepository : JpaRepository<UserTodoEntity, Long> {
    fun findByUserId(userId: UUID): List<UserTodoEntity>
}
