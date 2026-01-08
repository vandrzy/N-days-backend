package part2.ndbckend.learn.model.product

import jakarta.validation.constraints.Size

class UpdateProductRequest (
    @field:Size(max = 255, min = 3)
    val title: String? = null,
)