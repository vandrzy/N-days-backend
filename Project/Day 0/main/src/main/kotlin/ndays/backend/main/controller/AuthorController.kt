package ndays.backend.main.controller

import jakarta.validation.Valid
import ndays.backend.main.model.AuthorResponse
import ndays.backend.main.model.CreateAuthorRequest
import ndays.backend.main.model.UpdateAuthorRequest
import ndays.backend.main.model.WebResponse
import ndays.backend.main.service.AuthorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthorController(
    @Autowired
    val authorService: AuthorService
) {
    @PostMapping("/api/author")
    fun createAuthor(@Valid @RequestBody request: CreateAuthorRequest): WebResponse<AuthorResponse>{
        val authorResponse = authorService.addAuthor(request)
        return WebResponse(
            data = authorResponse,
            error = null
        )
    }

    @GetMapping("/api/author")
    fun getAuthor():WebResponse<List<AuthorResponse>>{
        val listAuthor = authorService.getAllAuthor()

        return WebResponse(data = listAuthor, error = null)
    }

    @PatchMapping("/api/author/{id}")
    fun updateAuthor(@PathVariable("id") id: String,
                     @RequestBody request:UpdateAuthorRequest):WebResponse<AuthorResponse>{
        val authorResponse = authorService.updateAuthor(id, request)

        return WebResponse(data = authorResponse, error = null)
    }

    @DeleteMapping("/api/author/{id}")
    fun deleteAuthor(@PathVariable("id") id:String):WebResponse<String>{
        authorService.deleteAuthor(id)
        return WebResponse(
            data = "Delete berhasil",
            error = null
        )
    }


}