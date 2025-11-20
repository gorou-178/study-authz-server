package org.example.study.controller.user

import org.example.study.dto.user.UserTodoResponse
import org.example.study.usecase.user.GetUserTodosUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/user")
class UserTodoController(
    private val getUserTodosUseCase: GetUserTodosUseCase,
) {
    @GetMapping("/{userId}/todos")
    fun getUserTodos(
        @PathVariable userId: UUID,
    ): ResponseEntity<List<UserTodoResponse>> {
        val todos = getUserTodosUseCase.execute(userId)
        val response = todos.map { UserTodoResponse.from(it) }
        return ResponseEntity.ok(response)
    }
}
