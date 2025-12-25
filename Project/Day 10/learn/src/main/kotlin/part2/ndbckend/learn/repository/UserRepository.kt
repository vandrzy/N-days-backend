package part2.ndbckend.learn.repository

import org.springframework.data.jpa.repository.JpaRepository
import part2.ndbckend.learn.entity.User

interface UserRepository: JpaRepository<User, String> {
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
}