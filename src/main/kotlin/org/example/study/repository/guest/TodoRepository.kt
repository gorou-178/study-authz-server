package org.example.study.repository.guest

import org.example.study.domain.model.Todo

interface TodoRepository {
    fun findRandomTodo(): Todo?

    fun findAll(): List<Todo>
}
