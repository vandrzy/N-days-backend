package part2.ndbckend.learn.repository

import org.springframework.data.jpa.repository.JpaRepository
import part2.ndbckend.learn.entity.Book

interface BookRepository : JpaRepository<Book, String> {
}