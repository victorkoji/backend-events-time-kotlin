package io.eventstime.controller

import io.eventstime.exception.CustomException
import io.eventstime.exception.EventErrorType
import io.eventstime.exception.ProductCategoryErrorType
import io.eventstime.mapper.toResponse
import io.eventstime.schema.ProductCategoryRequest
import io.eventstime.schema.ProductCategoryResponse
import io.eventstime.service.ProductCategoryService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/product-categories")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Product Categories")
class ProductCategoryController(
    private val productCategoryService: ProductCategoryService
) {
    @GetMapping("/")
    fun findAllProductCategories(): List<ProductCategoryResponse?> {
        return productCategoryService.findAll().toResponse()
    }

    @PostMapping("/")
    fun createProductCategory(@RequestBody standCategoryRequest: ProductCategoryRequest): ProductCategoryResponse {
        return productCategoryService.createProductCategory(standCategoryRequest).toResponse()
    }

    @GetMapping("/{standCategoryId}")
    fun findProductCategory(@PathVariable standCategoryId: Long): ProductCategoryResponse? {
        val standCategoryRequest = productCategoryService.findById(standCategoryId) ?: throw CustomException(ProductCategoryErrorType.PRODUCT_CATEGORY_NOT_FOUND)
        return standCategoryRequest.toResponse()
    }

    @PutMapping("/{standCategoryId}")
    fun updateProductCategory(
        @PathVariable standCategoryId: Long,
        @RequestBody standCategoryRequest: ProductCategoryRequest
    ): ProductCategoryResponse {
        return productCategoryService.updateProductCategory(standCategoryId, standCategoryRequest).toResponse()
    }

    @DeleteMapping("/{standCategoryId}")
    fun deleteProductCategory(@PathVariable standCategoryId: Long) {
        productCategoryService.deleteProductCategory(standCategoryId)
    }

    @ExceptionHandler
    fun handleException(e: Exception): ResponseStatusException = when (e) {
        is CustomException ->
            ResponseStatusException(
                when (e.message) {
                    ProductCategoryErrorType.PRODUCT_CATEGORY_NOT_FOUND.name,
                    EventErrorType.EVENT_NOT_FOUND.name
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
        private val log = LoggerFactory.getLogger(ProductCategoryController::class.java)!!
    }
}
