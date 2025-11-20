package org.example.study.repository.guest.impl

import org.example.study.domain.model.Todo
import org.example.study.repository.guest.TodoRepository
import org.example.study.repository.guest.jpa.TodoJpaRepository
import org.springframework.stereotype.Repository

@Repository
class TodoRepositoryImpl(
    private val todoJpaRepository: TodoJpaRepository,
) : TodoRepository {
    override fun findTodo(): Todo? {
        return todoJpaRepository.findTodo()?.toDomainModel()
    }

    override fun findAll(): List<Todo> {
        return todoJpaRepository.findAll().map { it.toDomainModel() }
    }
}
