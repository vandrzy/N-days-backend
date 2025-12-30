package part2.ndbckend.learn.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import part2.ndbckend.learn.entity.Book
import part2.ndbckend.learn.model.PageResponse
import part2.ndbckend.learn.model.WebResponse
import part2.ndbckend.learn.model.book.BookCountByAuthor
import part2.ndbckend.learn.model.book.BookResponse
import part2.ndbckend.learn.repository.BookRepository

@Service
class BookService (
    private val bookRepository: BookRepository
) {

    fun getAllNoPage(): WebResponse<List<BookResponse>>{
        val result = bookRepository.findAll().map {
            value -> toBookResponse(value)
        }

        return WebResponse(result)

    }

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




    private fun toBookResponse(book: Book): BookResponse{
        return BookResponse(
            id = book.id,
            judul = book.judul,
            penulis = book.penulis,
            tahunTerbit = book.tahunTerbit
        )
    }
}