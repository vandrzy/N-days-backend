package part2.ndbckend.learn.model.post

import jakarta.validation.constraints.Size

class UpdatePostRequest (
    @field:Size(max = 150)
    val title: String? = null,
    var description: String? = null

)