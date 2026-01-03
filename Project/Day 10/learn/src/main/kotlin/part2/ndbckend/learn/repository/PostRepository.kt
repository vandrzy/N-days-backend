package part2.ndbckend.learn.repository

import org.springframework.data.jpa.repository.JpaRepository
import part2.ndbckend.learn.entity.Post

interface PostRepository: JpaRepository<Post, String> {
    fun findByShortCode(shortCode: String): Post?
}