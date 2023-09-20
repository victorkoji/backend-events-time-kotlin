package io.eventstime.service

import io.eventstime.exception.*
import io.eventstime.model.Product
import io.eventstime.repository.ProductRepository
import io.eventstime.schema.ProductRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val standService: StandService,
    private val productCategoryService: ProductCategoryService,
    private val productFileService: ProductFileService
) {
    fun findAll(): List<Product?> {
        return productRepository.findAll()
    }

    fun findById(productId: Long): Product? {
        return productRepository.findByIdOrNull(productId)
    }

    fun uploadImage(productId: Long, file: MultipartFile): Product {
        val updatedProduct = findById(productId)?.let {
            val productFile = productFileService.createProductFile(it.productFile?.id, file)

            it.copy(
                productFile = productFile
            )
        } ?: throw CustomException(ProductErrorType.PRODUCT_NOT_FOUND)

        return productRepository.saveAndFlush(updatedProduct)
    }

    fun createProduct(productRequest: ProductRequest): Product {
        val productCategory = productCategoryService.findById(productRequest.productCategoryId) ?: throw CustomException(ProductCategoryErrorType.PRODUCT_CATEGORY_NOT_FOUND)
        val stand = standService.findById(productRequest.standId) ?: throw CustomException(StandErrorType.STAND_NOT_FOUND)

        return productRepository.saveAndFlush(
            Product(
                name = productRequest.name,
                price = productRequest.price,
                productCategory = productCategory,
                stand = stand
            )
        )
    }

    fun updateProduct(productId: Long, productRequest: ProductRequest): Product {
        val productCategory = productCategoryService.findById(productRequest.productCategoryId) ?: throw CustomException(ProductCategoryErrorType.PRODUCT_CATEGORY_NOT_FOUND)
        val stand = standService.findById(productRequest.standId) ?: throw CustomException(StandErrorType.STAND_NOT_FOUND)

        val updatedProduct = findById(productId)?.copy(
            id = productId,
            name = productRequest.name,
            price = productRequest.price,
            productCategory = productCategory,
            stand = stand
        ) ?: throw CustomException(ProductErrorType.PRODUCT_NOT_FOUND)

        return productRepository.saveAndFlush(updatedProduct)
    }

    fun deleteProduct(productId: Long) {
        findById(productId)?.let {
            productRepository.delete(it)
        } ?: throw CustomException(ProductErrorType.PRODUCT_NOT_FOUND)
    }
}
