package io.eventstime.controller

import io.eventstime.exception.*
import io.eventstime.mapper.toResponse
import io.eventstime.schema.ProductRequest
import io.eventstime.schema.ProductResponse
import io.eventstime.service.ProductService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/products")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Products")
class ProductController(
    private val productService: ProductService
) {
    @GetMapping("/")
    fun findAllProduct(): List<ProductResponse?> {
        return productService.findAll().toResponse()
    }

    @PostMapping("/")
    fun createProduct(@RequestBody productRequest: ProductRequest): ProductResponse? {
        return productService.createProduct(productRequest).toResponse()
    }

    @PostMapping("/{productId}/upload-image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadImageProduct(
        @PathVariable productId: Long,
        @RequestParam("file") file: MultipartFile
    ): ProductResponse? {
        return productService.uploadImage(productId, file).toResponse()
    }

    @GetMapping("/{productId}")
    fun findProduct(@PathVariable productId: Long): ProductResponse? {
        val productRequest = productService.findById(productId) ?: throw CustomException(ProductErrorType.PRODUCT_NOT_FOUND)
        return productRequest.toResponse()
    }

    @PutMapping("/{productId}")
    fun updateProduct(@PathVariable productId: Long, @RequestBody productRequest: ProductRequest): ProductResponse {
        return productService.updateProduct(productId, productRequest).toResponse()
    }

    @DeleteMapping("/{productId}")
    fun deleteProduct(@PathVariable productId: Long) {
        productService.deleteProduct(productId)
    }

    @ExceptionHandler
    fun handleException(e: Exception): ResponseStatusException = when (e) {
        is CustomException ->
            ResponseStatusException(
                when (e.message) {
                    ProductErrorType.PRODUCT_NOT_FOUND.name,
                    ProductCategoryErrorType.PRODUCT_CATEGORY_NOT_FOUND.name,
                    StandErrorType.STAND_NOT_FOUND.name
                    -> HttpStatus.NOT_FOUND
                    else -> HttpStatus.BAD_REQUEST
                },
                e.message.toString()
            )
        else -> {
            log.warn(e.message.toString())
            ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.name
            )
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProductController::class.java)!!
    }
}
