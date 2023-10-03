package io.eventstime.service

import io.eventstime.exception.CustomException
import io.eventstime.exception.EventErrorType
import io.eventstime.exception.ProductCategoryErrorType
import io.eventstime.model.MenuCategory
import io.eventstime.model.ProductCategory
import io.eventstime.repository.ProductCategoryRepository
import io.eventstime.schema.ProductCategoryRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProductCategoryService(
    private val productCategoryRepository: ProductCategoryRepository,
    private val eventService: EventService
) {
    fun findAll(): List<ProductCategory?> {
        return productCategoryRepository.findAll()
    }

    fun findById(productCategoryId: Long): ProductCategory? {
        return productCategoryRepository.findByIdOrNull(productCategoryId)
    }

    fun findMenuByEventId(eventId: Long): List<MenuCategory> {
        val categories = productCategoryRepository.findAllByEventIdOrderByNameAsc(eventId)

        return categories.map {
            MenuCategory(
                id = it.id!!,
                name = it.name,
                eventId = it.event?.id!!,
                products = it.products.orEmpty()
            )
        }
    }

    fun createProductCategory(productCategory: ProductCategoryRequest): ProductCategory {
        val event = eventService.findById(productCategory.eventId) ?: throw CustomException(EventErrorType.EVENT_NOT_FOUND)

        return productCategoryRepository.saveAndFlush(
            ProductCategory(
                name = productCategory.name,
                event = event
            )
        )
    }

    fun updateProductCategory(productCategoryId: Long, productCategory: ProductCategoryRequest): ProductCategory {
        val event = eventService.findById(productCategory.eventId) ?: throw CustomException(EventErrorType.EVENT_NOT_FOUND)

        val updatedProductCategory = findById(productCategoryId)?.copy(
            id = productCategoryId,
            name = productCategory.name,
            event = event
        ) ?: throw CustomException(ProductCategoryErrorType.PRODUCT_CATEGORY_NOT_FOUND)

        return productCategoryRepository.saveAndFlush(updatedProductCategory)
    }

    fun deleteProductCategory(productCategoryId: Long) {
        findById(productCategoryId)?.let { productCategory ->
            productCategoryRepository.delete(productCategory)
        } ?: throw CustomException(ProductCategoryErrorType.PRODUCT_CATEGORY_NOT_FOUND)
    }
}
