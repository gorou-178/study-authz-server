package org.example.study.controller.guest

import jakarta.validation.Valid
import org.example.study.dto.guest.CreateTodoRequest
import org.example.study.dto.guest.TodoResponse
import org.example.study.usecase.guest.CreateTodoUseCase
import org.example.study.usecase.guest.GetTodoUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/guest")
class GuestTodoController(
    private val getTodoUseCase: GetTodoUseCase,
    private val createTodoUseCase: CreateTodoUseCase,
) {
    @GetMapping("/todos")
    fun getTodo(): ResponseEntity<List<TodoResponse>> {
        return getTodoUseCase.execute()
            .map { todos -> todos.map { TodoResponse.from(it) } }
            .fold(
                onSuccess = { ResponseEntity.ok(it) },
                onFailure = { ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emptyList()) },
            )
    }

    @PostMapping("/todos")
    fun createTodo(
        @Valid @RequestBody request: CreateTodoRequest,
    ): ResponseEntity<TodoResponse> {
        return createTodoUseCase.execute(
            title = request.title!!,
            description = request.description!!,
        ).fold(
            onSuccess = { todo ->
                ResponseEntity.status(HttpStatus.CREATED).body(TodoResponse.from(todo))
            },
            onFailure = {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
            },
        )
    }
}
