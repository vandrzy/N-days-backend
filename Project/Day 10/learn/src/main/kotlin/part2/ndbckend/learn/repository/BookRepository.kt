package part2.ndbckend.learn.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import part2.ndbckend.learn.entity.Book
import part2.ndbckend.learn.model.book.BookCountByAuthor

interface BookRepository : JpaRepository<Book, String> {

    @Query("SELECT COUNT(b) FROM Book b")
    fun countBook(): Long

    @Query("SELECT MAX(b.tahunTerbit) FROM Book b")
    fun maxTahunTerbit(): Long

    @Query("SELECT MIN(b.tahunTerbit) FROM Book b")
    fun minTahunTerbit(): Long

    @Query("SELECT b.penulis AS penulis, COUNT(b) AS total FROM Book b GROUP BY b.penulis")
    fun countBookByAuthor(): List<BookCountByAuthor>
}