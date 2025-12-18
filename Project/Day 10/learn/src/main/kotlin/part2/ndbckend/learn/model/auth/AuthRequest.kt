package part2.ndbckend.learn.model.auth

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import part2.ndbckend.learn.entity.Role

class AuthRequest (
    @field:NotBlank
    @Size(min = 5, max = 12)
    val username: String,

    @field:NotBlank
    @Size(min= 5, max = 12)
    val password: String,

    @field:NotNull
    val role: Role
)
