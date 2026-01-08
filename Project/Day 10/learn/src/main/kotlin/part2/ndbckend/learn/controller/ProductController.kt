package part2.ndbckend.learn.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import part2.ndbckend.learn.model.WebResponse
import part2.ndbckend.learn.model.product.CreateProductRequest
import part2.ndbckend.learn.model.product.ProductResponse
import part2.ndbckend.learn.model.product.UpdateProductRequest
import part2.ndbckend.learn.service.ProductService
import tools.jackson.module.kotlin.jacksonObjectMapper

@RestController
class ProductController (
    private val productService: ProductService
) {

    @PostMapping(
        "/api/product", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    fun createProduct(@Valid @RequestPart("data") data: String,
                      @RequestPart("image", required = false) image: MultipartFile?)
    : ResponseEntity<WebResponse<ProductResponse>>{
        val request = jacksonObjectMapper().readValue(data, CreateProductRequest::class.java)
        val result = productService.createProduct(request, image)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(WebResponse(result))
    }

    @GetMapping("/api/product/{shortCode}")
    @PreAuthorize("isAuthenticated()")
    fun getProduct(@PathVariable("shortCode") shortCode: String)
    : ResponseEntity<WebResponse<ProductResponse>>{
        val result = productService.getProduct(shortCode)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(WebResponse(result))
    }

    @PatchMapping("/api/product/{shortCode}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("isAuthenticated()")
    fun updateProduct(@PathVariable("shortCode") shortCode: String,
                      @Valid @RequestPart("data") request: UpdateProductRequest,
                      @RequestPart("image", required = false) file: MultipartFile?)
    : ResponseEntity<WebResponse<ProductResponse>>{
        val result = productService.updateProduct(shortCode, request, file)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(WebResponse(result))
    }

    @DeleteMapping("/api/product/{shortCode}")
    @PreAuthorize("isAuthenticated()")
    fun deleteProduct(@PathVariable("shortCode") shortCode: String)
    : ResponseEntity<WebResponse<String>>{
        productService.deleteProduct(shortCode)
        return  ResponseEntity
            .status(HttpStatus.OK)
            .body(WebResponse("Berhasil Menghapus Data"))
    }


}