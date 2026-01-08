package part2.ndbckend.learn.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "products")
class Product (
    @Id
    @Column(name = "id")
    val id: String,
    @Column(name = "short_code", nullable = false, unique = true)
    val shortCode: String,
    @Column(name = "title")
    var title: String,
    @Column(name = "photo_id")
    var photoId: String? = null,
    @Column(name = "photo_url")
    var photoUrl: String? = null
): BaseEntity()