package part2.ndbckend.learn.controller

import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import part2.ndbckend.learn.model.WebResponse
import part2.ndbckend.learn.model.post.CreatePostRequest
import part2.ndbckend.learn.model.post.PostResponse
import part2.ndbckend.learn.model.post.UpdatePostRequest
import part2.ndbckend.learn.service.PostService
import tools.jackson.module.kotlin.jacksonObjectMapper

@RestController
class PostController (
    private val postService: PostService
) {

    @PreAuthorize("isAuthenticated()")
    @PostMapping("api/post", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createPost(
        @Valid @RequestPart("data") data: String,
        @RequestPart("photo", required = false) photo: MultipartFile?): WebResponse<PostResponse>{
        val createPostRequest = jacksonObjectMapper().readValue(data, CreatePostRequest::class.java)
        val result = postService.createPost(createPostRequest, photo)

        return WebResponse(
            result
        )
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("api/post/{shortCode}")
    fun updatePost(@PathVariable("shortCode") shortCode: String,
                   @Valid @RequestPart("data") data: String,
                   @RequestPart("photo", required = false) photo: MultipartFile?)
    : WebResponse<PostResponse>{
        val updatePostRequest = jacksonObjectMapper().readValue(data, UpdatePostRequest::class.java)
        val result= postService.updatePost(shortCode, updatePostRequest, photo)

        return WebResponse(
            result
        )
    }
}