package org.example.study.dto.user

import org.example.study.domain.model.Todo
import org.springframework.data.domain.Page

data class PagedUserTodoResponse(
    val content: List<UserTodoResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
) {
    companion object {
        fun from(page: Page<Todo>): PagedUserTodoResponse {
            return PagedUserTodoResponse(
                content = page.content.map { UserTodoResponse.from(it) },
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                currentPage = page.number,
                pageSize = page.size,
                hasNext = page.hasNext(),
                hasPrevious = page.hasPrevious(),
            )
        }
    }
}
