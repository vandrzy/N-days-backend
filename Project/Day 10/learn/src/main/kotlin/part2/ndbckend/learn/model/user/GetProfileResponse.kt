package part2.ndbckend.learn.model.user

import part2.ndbckend.learn.entity.Role

class GetProfileResponse (
    val username: String,
    val role: Role
)