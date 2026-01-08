package part2.ndbckend.learn.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import part2.ndbckend.learn.configuration.cloudinary.CloudinaryService
import part2.ndbckend.learn.entity.Product
import part2.ndbckend.learn.model.product.CreateProductRequest
import part2.ndbckend.learn.model.product.ProductResponse
import part2.ndbckend.learn.model.product.UpdateProductRequest
import part2.ndbckend.learn.repository.ProductRepository
import java.util.*

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val cloudinaryService: CloudinaryService
) {
    @Transactional
    fun createProduct(request: CreateProductRequest, image:MultipartFile?): ProductResponse{
        val id = "product-" + UUID.randomUUID().toString()
        val product = Product(
            id = id,
            title = request.title,
            shortCode = generateShortCode(id)
        )

        if (image != null && !image.isEmpty){
            validateFile(image)

            val uploadResult = cloudinaryService.uploadImage(
                image,
                folder = "products"
            )

            product.photoId = uploadResult.publicId
            product.photoUrl = uploadResult.secureUrl
        }

        productRepository.save(product)

        return toProductResponse(product)
    }

    @Transactional
    fun updateProduct(shortCode: String, request: UpdateProductRequest, image: MultipartFile?):
    ProductResponse{
        val product = findProduct(shortCode)

        request.title?.let {
            product.title = request.title
        }

        if (image != null && !image.isEmpty){
            validateFile(image)
            product.photoId?.let {
                cloudinaryService.deleteImage(it)
            }


            val uploadResult = cloudinaryService.uploadImage(
                image,
                "products"
            )

            product.photoId = uploadResult.publicId
            product.photoUrl = uploadResult.secureUrl
        }
        return toProductResponse(product)
    }

    @Transactional
    fun deleteProduct(shortCode: String){
        val product = findProduct(shortCode)

        product.photoId?.let {
            cloudinaryService.deleteImage(it)
        }

        productRepository.delete(product)
    }

    fun getProduct(shortCode: String): ProductResponse{
        val product = findProduct(shortCode)

        return toProductResponse(product)
    }

    private fun validateFile(file:MultipartFile){
        val allowedTypes = listOf("image/jpeg", "image/png")
        if (file.contentType !in allowedTypes) {
            throw IllegalArgumentException("Tipe file tidak didukung")
        }
        if (file.size > 5 * 1024 * 1024) {
            throw IllegalArgumentException("Ukuran file maksimal 5MB")
        }
    }

    private fun findProduct(shortCode: String): Product{
        return productRepository.findByShortCode(shortCode)
            ?: throw IllegalArgumentException("Product tidak ada")
    }

    private fun generateShortCode(id: String): String{
        return Base64.getUrlEncoder().encodeToString(id.toByteArray()).take(15)
    }

    private fun toProductResponse(product: Product): ProductResponse{
        return ProductResponse(
            id = product.id,
            shortCode = product.shortCode,
            title = product.title,
            photoId = product.photoId,
            photoUrl = product.photoUrl,
            createdAt = product.createdAt,
            updatedAt = product.updatedAt,
            createdBy = product.createdBy,
            updatedBy = product.updatedBy,
        )
    }
}