package part2.ndbckend.learn.repository

import org.springframework.data.jpa.repository.JpaRepository
import part2.ndbckend.learn.entity.Product

interface ProductRepository: JpaRepository<Product, String> {
    fun findByShortCode(shortCode: String): Product?
}