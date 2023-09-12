package io.eventstime.controller

import io.eventstime.exception.CustomException
import io.eventstime.exception.EventErrorType
import io.eventstime.exception.ProductCategoryErrorType
import io.eventstime.model.Event
import io.eventstime.model.ProductCategory
import io.eventstime.schema.ProductCategoryRequest
import io.eventstime.service.ProductCategoryService
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class ProductCategoryControllerTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: ProductCategoryController

    private val productCategoryService = mockk<ProductCategoryService>()

    private val productCategory = ProductCategory(
        id = 1,
        name = "event-1",
        event = Event(
            id = 1,
            name = "test",
            address = "test",
            isPublic = false,
            programmedDateInitial = LocalDate.now(),
            programmedDateFinal = LocalDate.now()
        )
    )

    @Test
    fun `Find all product categories with list not empty`() {
        // GIVEN
        every {
            productCategoryService.findAll()
        } returns listOf(productCategory)

        // WHEN
        val result = testObject.findAllProductCategories()

        // THEN
        Assertions.assertEquals(1, result.size)
        verify(exactly = 1) { productCategoryService.findAll() }
    }

    @Test
    fun `Find all product categories with list empty`() {
        // GIVEN
        every {
            productCategoryService.findAll()
        } returns emptyList()

        // WHEN
        val result = testObject.findAllProductCategories()

        // THEN
        Assertions.assertEquals(0, result.size)
        verify(exactly = 1) { productCategoryService.findAll() }
    }

    @Test
    fun `Create product category with success`() {
        // GIVEN
        val productCategoryRequest = ProductCategoryRequest(
            name = productCategory.name,
            eventId = productCategory.event?.id!!
        )

        every {
            productCategoryService.createProductCategory(productCategoryRequest)
        } returns productCategory

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.createProductCategory(productCategoryRequest)
        }

        // THEN
        verify(exactly = 1) { productCategoryService.createProductCategory(productCategoryRequest) }
    }

    @Test
    fun `Update product category with success`() {
        // GIVEN
        val productCategoryRequest = ProductCategoryRequest(
            name = productCategory.name,
            eventId = productCategory.event?.id!!
        )

        every {
            productCategoryService.updateProductCategory(productCategory.id!!, productCategoryRequest)
        } returns productCategory

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.updateProductCategory(productCategory.id!!, productCategoryRequest)
        }

        // THEN
        verify(exactly = 1) { productCategoryService.updateProductCategory(productCategory.id!!, productCategoryRequest) }
    }

    @Test
    fun `Delete product category with success`() {
        // GIVEN
        every {
            productCategoryService.deleteProductCategory(productCategory.id!!)
        } just runs

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.deleteProductCategory(productCategory.id!!)
        }

        // THEN
        verify(exactly = 1) { productCategoryService.deleteProductCategory(productCategory.id!!) }
    }

    @Test
    fun `Find product category with success`() {
        // GIVEN
        every {
            productCategoryService.findById(productCategory.id!!)
        } returns productCategory

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.findProductCategory(productCategory.id!!)
        }

        // THEN
        verify(exactly = 1) { productCategoryService.findById(productCategory.id!!) }
    }

    @Nested
    inner class HandleErrorTest {

        @Test
        fun `Handle error NOT_FOUND`() {
            // GIVEN
            val exceptionProductCategoryNotFound = CustomException(ProductCategoryErrorType.PRODUCT_CATEGORY_NOT_FOUND)
            val exceptionEventNotFound = CustomException(EventErrorType.EVENT_NOT_FOUND)

            // WHEN
            val resultProductCategoryNotFound = testObject.handleException(exceptionProductCategoryNotFound)
            val resultEventNotFound = testObject.handleException(exceptionEventNotFound)

            // THEN
            Assertions.assertEquals(HttpStatus.NOT_FOUND, resultProductCategoryNotFound.statusCode)
            Assertions.assertEquals(HttpStatus.NOT_FOUND, resultEventNotFound.statusCode)
        }

        @Test
        fun `Handle error BAD_REQUEST`() {
            // GIVEN
            val exception = CustomException()

            // WHEN
            val result = testObject.handleException(exception)

            // THEN
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        }

        @Test
        fun `Handle error generic exception`() {
            // GIVEN
            val exception = Exception("error")

            // WHEN
            val result = testObject.handleException(exception)

            // THEN
            Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.statusCode)
        }
    }
}
