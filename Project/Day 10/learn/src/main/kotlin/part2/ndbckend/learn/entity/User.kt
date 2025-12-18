package part2.ndbckend.learn.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User (
    @Id
    val id: String,
    @Column(name = "username")
    var username: String,
    @Column(name = "password")
    var password: String,
    @Column(name = "role")
    @Enumerated(value = EnumType.STRING)
    val role: Role
)