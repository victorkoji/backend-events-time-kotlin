package io.eventstime.controller

import io.eventstime.exception.CustomException
import io.eventstime.mapper.toResponse
import io.eventstime.model.*
import io.eventstime.schema.MenuResponse
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
class MobileControllerTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: MobileController

    private val productCategoryService = mockk<ProductCategoryService>()

    private val menuCategory = MenuCategory(
        id = 1,
        name = "event-1",
        eventId = 1L,
        products = listOf(
            Product(
                id = 1,
                name = "product-1",
                price = 2F,
                productCategory = ProductCategory(
                    id = 1,
                    name = "product category"
                ),
                stand = Stand(
                    id = 1,
                    name = "stand",
                    isCashier = true,
                    event = Event(
                        id = 1L,
                        name = "event-1",
                        address = "",
                        isPublic = false,
                        programmedDateInitial = LocalDate.now(),
                        programmedDateFinal = LocalDate.now()
                    ),
                    standCategory = StandCategory(
                        id = 1L,
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
                )
            )
        )
    )

    @Test
    fun `Find menu by event id with success`() {
        // GIVEN
        val eventId = 1L
        every {
            productCategoryService.findMenuByEventId(eventId)
        } returns listOf(menuCategory)

        // WHEN
        val result = testObject.findProductMenu(eventId)

        // THEN
        Assertions.assertEquals(listOf(menuCategory.toResponse()), result)
        verify(exactly = 1) { productCategoryService.findMenuByEventId(eventId) }
    }

    @Test
    fun `Find menu by event id returns empty list`() {
        // GIVEN
        val eventId = 1L
        every {
            productCategoryService.findMenuByEventId(eventId)
        } returns emptyList()

        // WHEN
        val result = testObject.findProductMenu(eventId)

        // THEN
        Assertions.assertEquals(emptyList<MenuResponse>(), result)
        verify(exactly = 1) { productCategoryService.findMenuByEventId(eventId) }
    }

    @Nested
    inner class HandleErrorTest {
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
