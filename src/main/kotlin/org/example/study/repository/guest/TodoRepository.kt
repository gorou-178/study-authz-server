package org.example.study.repository.guest

import org.example.study.domain.model.Todo

interface TodoRepository {
    fun findTodo(): Todo?

    fun findAll(): List<Todo>
}
