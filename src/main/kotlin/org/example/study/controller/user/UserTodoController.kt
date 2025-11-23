package org.example.study.controller.user

import jakarta.validation.Valid
import org.example.study.dto.user.CreateUserTodoRequest
import org.example.study.dto.user.PagedUserTodoResponse
import org.example.study.dto.user.UserTodoResponse
import org.example.study.usecase.user.CreateUserTodoUseCase
import org.example.study.usecase.user.GetUserTodosUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/user")
class UserTodoController(
    private val getUserTodosUseCase: GetUserTodosUseCase,
    private val createUserTodoUseCase: CreateUserTodoUseCase,
) {
    @GetMapping("/{userId}/todos")
    fun getUserTodos(
        @PathVariable userId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<PagedUserTodoResponse> {
        val todosPage = getUserTodosUseCase.execute(userId, page, size)
        val response = PagedUserTodoResponse.from(todosPage)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{userId}/todos")
    fun createUserTodo(
        @PathVariable userId: UUID,
        @Valid @RequestBody request: CreateUserTodoRequest,
    ): ResponseEntity<UserTodoResponse> {
        return createUserTodoUseCase.execute(
            userId = userId,
            title = request.title!!,
            description = request.description!!,
        ).fold(
            onSuccess = { todo ->
                ResponseEntity.status(HttpStatus.CREATED).body(UserTodoResponse.from(todo))
            },
            onFailure = {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
            },
        )
    }
}
