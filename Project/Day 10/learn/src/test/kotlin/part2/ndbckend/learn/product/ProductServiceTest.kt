package part2.ndbckend.learn.product

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.web.multipart.MultipartFile
import part2.ndbckend.learn.configuration.cloudinary.CloudinaryService
import part2.ndbckend.learn.configuration.cloudinary.CloudinaryUploadResponse
import part2.ndbckend.learn.entity.Product
import part2.ndbckend.learn.model.product.CreateProductRequest
import part2.ndbckend.learn.model.product.UpdateProductRequest
import part2.ndbckend.learn.repository.ProductRepository
import part2.ndbckend.learn.service.ProductService
import kotlin.test.assertEquals


@ExtendWith(MockitoExtension::class)
class ProductServiceTest {

    @Mock
    lateinit var productRepository: ProductRepository

    @Mock
    lateinit var cloudinaryService: CloudinaryService

    @InjectMocks
    lateinit var productService: ProductService


    @Test
    fun `upload without image (createProduct())`(){
        val request = CreateProductRequest(
            title = "Macbook Pro"
        )

        whenever(productRepository.save(any()))
            .thenAnswer{it.arguments[0]}

        val response = productService.createProduct(request, null)

        assertEquals("Macbook Pro", response.title)

        assertNull(response.photoUrl)

        verify(cloudinaryService, never()).uploadImage(any(), any())
        verify(productRepository, times(1)).save(any())
    }

    @Test
    fun `upload with image (createProduct())`(){
        val request = CreateProductRequest(
            title = "Macbook Pro"
        )
        val file = mock<MultipartFile>()
        whenever(file.isEmpty).thenReturn(false)
        whenever(file.contentType).thenReturn("image/png")
        whenever(file.size).thenReturn(1024)

        val uploadResponse = CloudinaryUploadResponse(
            publicId = "product-123",
            secureUrl = "https://cloudinary.com/product.png"
        )

        whenever(cloudinaryService.uploadImage(file, "product"))
            .thenReturn(uploadResponse)

        whenever(productRepository.save(any()))
            .thenAnswer{it.arguments[0]}

        val result = productService.createProduct(request,file)

        assertEquals("Macbook Pro", result.title)
        assertEquals("https://cloudinary.com/product.png", result.photoUrl)

        verify(cloudinaryService, times(1))
            .uploadImage(file, "product")

        verify(productRepository, times(1))
            .save(any())

    }

    @Test
    fun `upload image ada foto lama dan baru (uploadImage())`(){
        val oldProduct = Product(
            id = "product-1",
            shortCode = "PRD-1",
            title = "Old title",
            photoId = "old-photo-id",
            photoUrl = "old-photo-url"
        )

        whenever(productRepository.findByShortCode("PRD-1"))
            .thenReturn(oldProduct)

        val file = mock<MultipartFile>()
        whenever(file.isEmpty).thenReturn(false)
        whenever(file.contentType).thenReturn("image/png")

        val uploadResponse = CloudinaryUploadResponse(
            publicId = "new-photo-id",
            secureUrl = "new-photo-url"
        )

        whenever(cloudinaryService.uploadImage(file, "products"))
            .thenReturn(uploadResponse)


        val request = UpdateProductRequest(
            "New title"
        )

        val result = productService.updateProduct("PRD-1", request, file)

        assertEquals("New title", result.title)
        assertEquals("new-photo-url", result.photoUrl)

        verify(cloudinaryService).deleteImage("old-photo-id")
        verify(cloudinaryService).uploadImage(file, "products")
    }
}