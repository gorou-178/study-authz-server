package org.example.study.dto.guest

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateTodoRequest(
    @field:NotBlank(message = "タイトルは必須です")
    @field:Size(min = 1, max = 100, message = "タイトルは1文字以上100文字以下で入力してください")
    val title: String?,
    @field:NotBlank(message = "説明は必須です")
    @field:Size(min = 1, max = 500, message = "説明は1文字以上500文字以下で入力してください")
    val description: String?,
)
