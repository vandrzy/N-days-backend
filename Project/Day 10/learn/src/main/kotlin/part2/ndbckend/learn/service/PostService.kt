package part2.ndbckend.learn.service

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.stereotype.Service
import part2.ndbckend.learn.entity.Post
import part2.ndbckend.learn.exeption.ResourceNotFoundException
import part2.ndbckend.learn.model.post.CreatePostRequest
import part2.ndbckend.learn.model.post.PostResponse
import part2.ndbckend.learn.model.post.UpdatePostRequest
import part2.ndbckend.learn.repository.PostRepository
import java.util.Base64
import java.util.UUID

@Service
class PostService(
    private val postRepository: PostRepository
) {

    fun createPost(request: CreatePostRequest): PostResponse {
        val id = "post-" + UUID.randomUUID().toString()
        val shortCode = createShortCode(id)
        val post = Post(
            id = id,
            title = request.title,
            shortCode = shortCode,
            description = request.description,
        )

        postRepository.save(post)

        return toPostResponse(post)

    }

    fun updatePost(shortCode: String, request: UpdatePostRequest): PostResponse{
        val post = postRepository.findByShortCode(shortCode)
        var isUpdated = false
        if (request.title != null){
            post!!.title = request.title
            isUpdated = true
        }
        if (request.description != null){
            post!!.description = request.description
            isUpdated = true
        }

        if(isUpdated){
            postRepository.save(post!!)
        }

        return toPostResponse(post!!)

    }


    private fun toPostResponse(post: Post): PostResponse{
        return PostResponse(
            title = post.title,
            description = post.description,
            shortCode = post.shortCode

        )
    }

    private fun createShortCode(id: String): String{
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(id.toByteArray())
            .take(8)
    }

}