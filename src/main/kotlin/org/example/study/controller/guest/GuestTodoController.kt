package org.example.study.controller.guest

import org.example.study.dto.guest.TodoResponse
import org.example.study.usecase.guest.GetTodoUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/guest")
class GuestTodoController(
    private val getTodoUseCase: GetTodoUseCase,
) {
    @GetMapping("/todos")
    fun getTodo(): ResponseEntity<TodoResponse> {
        val todo =
            getTodoUseCase.execute()
                ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(TodoResponse.from(todo))
    }
}
