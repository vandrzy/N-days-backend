# File Upload Local

Melakukan file upload dari salah satu endpoint untuk disimpan di direktori lokal 
(cara yang kurang disarankan, sebaiknya di cloud storage / CDN)

## Implementasi Upload Pada Service
```kotlin
@Service
class PostService(
    private val postRepository: PostRepository
) {

    private val uploadDir = Paths.get("uploads") 

    init { // untuk mengecek ada tidak nya direktory upload
        if (!Files.exists(uploadDir)){
            Files.createDirectories(uploadDir) // membuat direktory jika tidak ada
        }
    }

    fun createPost(request: CreatePostRequest, file:MultipartFile?): PostResponse {
        var fileName: String? = null
        var targetPath: Path? = null
        val id = "post-" + UUID.randomUUID().toString()
        val shortCode = createShortCode(id)

        if (file != null) {
            validateFile(file)
            fileName = generateFileName(file, id)
            targetPath = uploadDir.resolve(fileName)

            Files.copy (
                file.inputStream,
                targetPath!!,
                StandardCopyOption.REPLACE_EXISTING
            )
        }

        val post = Post(
            id = id,
            title = request.title,
            shortCode = shortCode,
            description = request.description,
            photoName = fileName,
            photoPath = targetPath.let { value ->
                when (value){
                    null -> null
                    else -> value.toString()
                }
            }
        )

        postRepository.save(post)

        return toPostResponse(post)

    }
}
```
- `validateFile(file)` mengecek file menggunakan helper function
- `targetPath = uploadDir.resolve(fileName)` menentukan lokasi penyimpanan file
- `Files.copy(
    file.inputStream,
    targetPath!!,
    StandardCopyOption.REPLACE_EXISTING
)
` bertujuan mengupload

## Implementasi Update pada Service
```kotlin
@Service
class PostService(
    private val postRepository: PostRepository
) {
    @Transactional
    fun updatePost(shortCode: String, request: UpdatePostRequest, file: MultipartFile?): PostResponse {
        val post = postRepository.findByShortCode(shortCode)
            ?: throw IllegalArgumentException("Post not found")
        request.title?.let {
            post.title = it
        }
        request.description?.let {
            post.description = it
        }

        if (file != null && !file.isEmpty) {
            validateFile(file)
            post.photoPath?.let { oldPath -> deleteFileSafely(oldPath) }

            val newFile = generateFileName(file, post.id)
            val newPathFile = uploadDir.resolve(newFile)

            Files.copy(file.inputStream, newPathFile, StandardCopyOption.REPLACE_EXISTING)

            post.photoPath = newPathFile.toString()
            post.photoName = newFile
        }

        return toPostResponse(post)

    }
}
```
- Hampir sama dengan upload file, perbedaanya terdapat penghapusan file lama jika ada

## Helper Function pada Service
```kotlin
@Service
class PostService(
    private val postRepository: PostRepository
) {
    private fun validateFile(file:MultipartFile){
        if (file.isEmpty){
            throw IllegalArgumentException("File tidak boleh kosong")
        }
        val allowedTypes = listOf("image/jpeg", "image/png")
        if (file.contentType !in allowedTypes) {
            throw IllegalArgumentException("Tipe file tidak didukung")
        }
        if (file.size > 5 * 1024 * 1024) {
            throw IllegalArgumentException("Ukuran file maksimal 5MB")
        }
    }

    private fun deleteFileSafely(oldPath: String){
        val path = Paths.get(oldPath)
        Files.deleteIfExists(path)
    }

    private fun generateFileName(file: MultipartFile, postId:String): String{
        val extension = file.originalFilename?.substringAfterLast('.', "")
        return "$postId-photo-${UUID.randomUUID()}.$extension"
    }

    private fun toPostResponse(post: Post): PostResponse{
        return PostResponse(
            title = post.title,
            description = post.description,
            shortCode = post.shortCode,
            photoPath = post.photoPath

        )
    }

    private fun createShortCode(id: String): String{
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(id.toByteArray())
            .take(8)
    }

}
```

## Implementasi pada Controller
```kotlin
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
```

- `consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]` agar dapat mengirimkan file lewat request body, strukturnya:
  - ```csharp
      multipart/form-data
      ├─ data   → JSON string
      └─ photo  → binary file
  ```
- `@RequestPart` Mengambil bagian tertentu dari multipart request, dapat berupa string, file, json
- `@Valid @RequestPart("data") data: String` bertipe string, karena postman mengirim data sebagai text (pada project asli langsung gunakan dto / model, string pada parameter tersebut dikarenakan postman hanya bisa mengirimkan sebagi text)
- `val createPostRequest = jacksonObjectMapper().readValue(data, CreatePostRequest::class.java)` mengubah request body (data) yang berupa string menjadi dto/ model yang diperlukan



