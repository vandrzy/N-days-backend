package part2.ndbckend.learn.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table(name = "posts")
class Post (
    @Id
    @Column(name = "id")
    val id: String,
    @Column(name = "short_code", unique = true, nullable = false)
    val shortCode: String,
    @Column(name = "title")
    var title: String,
    @Column(name = "description", nullable = true, columnDefinition = "TEXT")
    var description: String?,

    @Column(name = "photo_name", nullable = true)
    var photoName: String?,
    @Column(name = "photo_path", nullable = true)
    var photoPath: String?
): BaseEntity()