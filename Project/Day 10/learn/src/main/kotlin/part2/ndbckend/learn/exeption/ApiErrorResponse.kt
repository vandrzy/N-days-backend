package part2.ndbckend.learn.exeption

import java.time.LocalDateTime

class ApiErrorResponse (
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String?,
    val path: String
)