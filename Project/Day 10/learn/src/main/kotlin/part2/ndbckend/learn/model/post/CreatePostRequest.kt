package part2.ndbckend.learn.model.post

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

class CreatePostRequest (
    @field:NotNull
    @field:Size(max = 150)
    var title: String,

    var description: String? = null
)