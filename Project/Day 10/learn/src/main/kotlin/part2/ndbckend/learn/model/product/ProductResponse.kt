package part2.ndbckend.learn.model.product

import java.time.LocalDateTime

class ProductResponse (
    val id: String,
    val shortCode: String,
    val title: String,
    val photoId: String?,
    val photoUrl: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val createdBy: String?,
    val updatedBy: String?,
)