package io.eventstime.service

import io.eventstime.exception.CustomException
import io.eventstime.exception.EventErrorType
import io.eventstime.exception.ProductCategoryErrorType
import io.eventstime.exception.StandCategoryErrorType
import io.eventstime.model.Event
import io.eventstime.model.ProductCategory
import io.eventstime.repository.ProductCategoryRepository
import io.eventstime.schema.ProductCategoryRequest
import io.eventstime.schema.StandCategoryRequest
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class ProductCategoryServiceTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: ProductCategoryService

    private val productCategoryRepository = mockk<ProductCategoryRepository>()
    private val eventService = mockk<EventService>()

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
    fun `Find all product categories list not empty`() {
        // GIVEN
        every {
            productCategoryRepository.findAll()
        } returns listOf(productCategory)

        // WHEN
        val result = testObject.findAll()

        // THEN
        assertEquals(1, result.size)
        assertEquals(1, productCategory.id)

        verify(exactly = 1) { productCategoryRepository.findAll() }
    }

    @Test
    fun `Find all product categories list empty`() {
        // GIVEN
        every {
            productCategoryRepository.findAll()
        } returns emptyList()

        // WHEN
        val result = testObject.findAll()

        // THEN
        assertEquals(0, result.size)
        verify(exactly = 1) { productCategoryRepository.findAll() }
    }

    @Test
    fun `Find product category by id return object product category`() {
        // GIVEN
        every {
            productCategoryRepository.findById(productCategory.id!!)
        } returns Optional.of(productCategory)

        // WHEN
        val result = testObject.findById(productCategory.id!!)

        // THEN
        assertNotNull(result)

        verify(exactly = 1) { productCategoryRepository.findById(productCategory.id!!) }
    }

    @Test
    fun `Find product category by id return null`() {
        // GIVEN
        val productCategoryId = 1L

        every {
            productCategoryRepository.findById(productCategoryId)
        } returns Optional.empty()

        // WHEN
        val result = testObject.findById(productCategoryId)

        // THEN
        assertNull(result)
        verify(exactly = 1) { productCategoryRepository.findById(productCategoryId) }
    }

    @Test
    fun `Create product category with success`() {
        // GIVEN
        val productCategoryRequest = ProductCategoryRequest(
            name = productCategory.name,
            eventId = productCategory.event?.id!!
        )

        every {
            eventService.findById(productCategory.event?.id!!)
        } returns productCategory.event

        every {
            productCategoryRepository.saveAndFlush(productCategory.copy(id = null))
        } returns productCategory

        // WHEN
        val result = testObject.createProductCategory(productCategoryRequest)

        // THEN
        assertNotNull(result)
        verify(exactly = 1) { productCategoryRepository.saveAndFlush(productCategory.copy(id = null)) }
    }

    @Test
    fun `Create product category with event not found error `() {
        // GIVEN
        val productCategoryRequest = ProductCategoryRequest(
            name = productCategory.name,
            eventId = productCategory.event?.id!!
        )

        every {
            eventService.findById(productCategory.event?.id!!)
        } returns null

        // WHEN
        val result = assertThrows<CustomException> { testObject.createProductCategory(productCategoryRequest) }

        // THEN
        assertEquals(EventErrorType.EVENT_NOT_FOUND.name, result.message)
        verify(exactly = 0) { productCategoryRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Create product category with error to save`() {
        // GIVEN
        val productCategoryRequest = ProductCategoryRequest(
            name = productCategory.name,
            eventId = productCategory.event?.id!!
        )

        every {
            eventService.findById(productCategory.event?.id!!)
        } returns productCategory.event

        every {
            productCategoryRepository.saveAndFlush(any())
        } throws Exception("Failure to save")

        // WHEN
        val result = assertThrows<Exception> { testObject.createProductCategory(productCategoryRequest) }

        // THEN
        assertEquals("Failure to save", result.message)
        verify(exactly = 1) { productCategoryRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Update product category with success`() {
        // GIVEN
        val productCategoryRequest = ProductCategoryRequest(
            name = productCategory.name,
            eventId = productCategory.event?.id!!
        )

        every {
            eventService.findById(productCategory.event?.id!!)
        } returns productCategory.event

        every {
            productCategoryRepository.findById(productCategory.id!!)
        } returns Optional.of(productCategory)

        every {
            productCategoryRepository.saveAndFlush(productCategory)
        } returns productCategory

        // WHEN
        val result = testObject.updateProductCategory(productCategory.id!!, productCategoryRequest)

        // THEN
        assertNotNull(result)
        verify(exactly = 1) { productCategoryRepository.saveAndFlush(productCategory) }
    }

    @Test
    fun `Update product category with error product category not found`() {
        // GIVEN
        val productCategoryRequest = ProductCategoryRequest(
            name = productCategory.name,
            eventId = productCategory.event?.id!!
        )

        every {
            eventService.findById(productCategory.event?.id!!)
        } returns productCategory.event

        every {
            productCategoryRepository.findById(productCategory.id!!)
        } returns Optional.empty()

        every {
            productCategoryRepository.saveAndFlush(productCategory)
        } returns productCategory

        // WHEN
        val result = assertThrows<CustomException> {
            testObject.updateProductCategory(productCategory.id!!, productCategoryRequest)
        }

        // THEN
        assertEquals(ProductCategoryErrorType.PRODUCT_CATEGORY_NOT_FOUND.name, result.message)
        verify(exactly = 0) { productCategoryRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Update product category with event not found error`() {
        // GIVEN
        val productCategoryRequest = ProductCategoryRequest(
            name = productCategory.name,
            eventId = productCategory.event?.id!!
        )

        every {
            eventService.findById(productCategory.event?.id!!)
        } returns null

        every {
            productCategoryRepository.findById(productCategory.id!!)
        } returns Optional.empty()

        every {
            productCategoryRepository.saveAndFlush(productCategory)
        } returns productCategory

        // WHEN
        val result = assertThrows<CustomException> {
            testObject.updateProductCategory(productCategory.id!!, productCategoryRequest)
        }

        // THEN
        assertEquals(EventErrorType.EVENT_NOT_FOUND.name, result.message)
        verify(exactly = 0) { productCategoryRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Delete product category with success`() {
        // GIVEN
        every {
            productCategoryRepository.findById(productCategory.id!!)
        } returns Optional.of(productCategory)

        every {
            productCategoryRepository.delete(productCategory)
        } just runs

        // WHEN
        val result = testObject.deleteProductCategory(productCategory.id!!)

        // THEN
        assertNotNull(result)
        verify(exactly = 1) { productCategoryRepository.delete(productCategory) }
    }

    @Test
    fun `Delete product category with error product category not found`() {
        // GIVEN
        every {
            productCategoryRepository.findById(productCategory.id!!)
        } returns Optional.empty()

        every {
            productCategoryRepository.delete(productCategory)
        } just runs

        // WHEN
        val result = assertThrows<CustomException> { testObject.deleteProductCategory(productCategory.id!!) }

        // THEN
        assertEquals(ProductCategoryErrorType.PRODUCT_CATEGORY_NOT_FOUND.name, result.message)
        verify(exactly = 0) { productCategoryRepository.delete(any()) }
    }
}
