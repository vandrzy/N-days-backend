# Aggregation Query

Query yang digunakan untuk mengolah sekumpulan data menjadi nilai ringkasan (summary), misalnya total, rata-rata, jumlah data, nilai maksimum, atau minimum.

## Fungsi
- Menghitung jumlah data `COUNT`
- Menghitung total nilai `SUM`
- Menghitung rata-rata `AVG`
- Mencari nilai terbesar / terkecil `MAX, MIN`
- Mengelompokkan data `GROUP BY`
- Memfilter hasil agregasi `HAVING`

## Impementasi pada Repository
```kotlin
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
```
- Menggunakan JPQL Query
- Untuk `countBookByAuthor` menggunakan DTO khusus
- BookCountByAuthor
    ```kotlin
  class BookCountByAuthor (
    val penulis: String?,
    val total: Long?
    )
    ```
  
## Impementasi pada Service
```kotlin
@Service
class BookService (
    private val bookRepository: BookRepository
) {
    fun getStatistic():Map<String, Any?>{
        return mapOf(
            "totalBooks" to bookRepository.countBook(),
            "minYear" to bookRepository.minTahunTerbit(),
            "maxYear" to bookRepository.maxTahunTerbit(),
        )
    }

    fun countBookPerAuthor(): List<BookCountByAuthor>{
        return bookRepository.countBookByAuthor()
    }
}
```

## Impementasi pada Controller
```kotlin
@RestController
class BookController (
    private val bookService: BookService
) {
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/book/stats")
    fun getStatistic(): WebResponse<Map<String, Any?>>{
        val response = bookService.getStatistic()

        return WebResponse(
            response
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/book/author")
    fun getBookPerAuthor(): WebResponse<List<BookCountByAuthor>>{
        val response = bookService.countBookPerAuthor()

        return WebResponse(
            response
        )
    }

}
```









