package part2.ndbckend.learn.controller

import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import part2.ndbckend.learn.model.WebResponse
import part2.ndbckend.learn.model.post.CreatePostRequest
import part2.ndbckend.learn.model.post.PostResponse
import part2.ndbckend.learn.model.post.UpdatePostRequest
import part2.ndbckend.learn.service.PostService

@RestController
class PostController (
    private val postService: PostService
) {

    @PreAuthorize("isAuthenticated()")
    @PostMapping("api/post")
    fun createPost(@Valid @RequestBody createPostRequest: CreatePostRequest): WebResponse<PostResponse>{
        val result = postService.createPost(createPostRequest)

        return WebResponse(
            result
        )
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("api/post/{shortCode}")
    fun updatePost(@PathVariable("shortCode") shortCode: String,
                   @Valid @RequestBody updatePostRequest: UpdatePostRequest)
    : WebResponse<PostResponse>{
        val result= postService.updatePost(shortCode, updatePostRequest)

        return WebResponse(
            result
        )
    }
}