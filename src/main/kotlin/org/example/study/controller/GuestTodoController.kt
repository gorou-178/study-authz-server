package org.example.study.controller

import org.example.study.controller.dto.TodoResponse
import org.example.study.usecase.GetRandomTodoUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/guest")
class GuestTodoController(
    private val getRandomTodoUseCase: GetRandomTodoUseCase,
) {
    @GetMapping("/todos")
    fun getRandomTodo(): ResponseEntity<TodoResponse> {
        val todo =
            getRandomTodoUseCase.execute()
                ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(TodoResponse.from(todo))
    }
}
