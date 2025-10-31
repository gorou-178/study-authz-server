package org.example.study.domain.repository

import org.example.study.domain.model.Todo

interface TodoRepository {
    fun findRandomTodo(): Todo?

    fun findAll(): List<Todo>
}
