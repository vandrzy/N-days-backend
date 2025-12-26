# Pagination & Sorting

## Pagination
Teknik membagi data menjadi beberapa halaman, sehingga tidak semua data dikirim sekaligus dalam satu request.

## Sorting
Proses mengurutkan data berdasarkan field tertentu.

## Implementasi pada Service
```kotlin
@Service
class BookService (
    private val bookRepository: BookRepository
) {
    fun getALl(
        page: Int,
        size: Int,
        sortBy: String,
        direction: String
    ): PageResponse<BookResponse>{
        val sort = if (direction.equals("desc", true))
            Sort.by(sortBy).descending()
        else
            Sort.by(sortBy).ascending()

        val pageable =PageRequest.of(page,size,sort)

        val result = bookRepository.findAll(pageable)

        return PageResponse(
            data = result.content.map { value -> toBookResponse(value) },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPage = result.totalPages
        )
    }
    
    private fun toBookResponse(book: Book): BookResponse{
        return BookResponse(
            id = book.id,
            judul = book.judul,
            penulis = book.penulis,
            tahunTerbit = book.tahunTerbit
        )
    }
}
```

### Fungsi `getALL()`
Mengambil data buku dengan pagination + sorting, lalu:
- Mapping entity Book
- Menjadi DTO BookResponse
- Dibungkus dalam response pagination custom PageResponse

### Logika Sorting
```kotlin
val sort = if (direction.equals("desc", true))
    Sort.by(sortBy).descending()
else
    Sort.by(sortBy).ascending()
```
Bertujuan untuk menentukan arah sorting menggunkanan sorting bawaan Spring data JPA

### Membuat Pageable & Query ke database
```kotlin
val pageable =PageRequest.of(page,size,sort)

val result = bookRepository.findAll(pageable)
```
- Membuat object pageable yang digunakan sebagai parameter (argumen) query ke database
- page → halaman (dimulai dari 0)
- size → jumlah data per halaman
- sort → urutan data
- Objek yang dihasilkan dari hasil query yaitu `Page<Book>`, berisi data entity (Book) dan metadata pagination
- Objek tersebut memiliki properti
  - Data utama
    ```kotlin
    result.content       // List<Book>
    ```
  - Informasi halaman
    ```kotlin
    result.number        // nomor halaman (0-based)
    result.size          // jumlah data per halaman
    result.totalElements // total seluruh data
    result.totalPages    // total halaman
    ```
  - Informasi tambahan
    ```kotlin
    result.first         // apakah halaman pertama
    result.last          // apakah halaman terakhir
    result.hasNext()     // apakah ada halaman berikutnya
    result.hasPrevious() // apakah ada halaman sebelumnya
    ```

### Implementasi pada Controller
```kotlin
@RestController
class BookController (
    private val bookService: BookService
) {
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/book_pageable")
    fun getAllBookPage(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "judul") sortBy: String,
        @RequestParam(defaultValue = "asc") direction:String
    ): PageResponse<BookResponse>{
        val result = bookService.getALl(page, size, sortBy, direction)
        return result
    }
}
```
- Contoh endpoint:
    ```
    http://localhost:8080/api/book_pageable?page=19&size=5&sortBy=tahunTerbit&direction=desc
    ```




