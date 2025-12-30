package part2.ndbckend.learn.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import part2.ndbckend.learn.model.PageResponse
import part2.ndbckend.learn.model.WebResponse
import part2.ndbckend.learn.model.book.BookCountByAuthor
import part2.ndbckend.learn.model.book.BookResponse
import part2.ndbckend.learn.service.BookService


@RestController
class BookController (
    private val bookService: BookService
) {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/book_no_page")
    fun getALlBookNoPage(): WebResponse<List<BookResponse>>{
        val result = bookService.getAllNoPage()
        return result
    }

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