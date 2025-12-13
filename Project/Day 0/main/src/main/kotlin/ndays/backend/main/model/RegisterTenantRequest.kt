package ndays.backend.main.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class RegisterTenantRequest (
    @field:NotBlank
    @Size(max = 15, min = 3)
    val username: String,

    @field:NotBlank
    @Size(min = 5, max = 15)
    val password: String
)