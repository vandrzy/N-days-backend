package part2.ndbckend.learn.model.product

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class CreateProductRequest (
    @field:NotBlank
    @field:Size(max = 255, min = 3)
    val title: String,
)