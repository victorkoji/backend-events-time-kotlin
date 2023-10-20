package io.eventstime.controller

import io.eventstime.auth.AuthorizationService
import io.eventstime.dto.UserEventStandDto
import io.eventstime.exception.CustomException
import io.eventstime.mapper.toEventResponse
import io.eventstime.mapper.toResponse
import io.eventstime.model.*
import io.eventstime.schema.EventResponse
import io.eventstime.schema.MenuResponse
import io.eventstime.service.ProductCategoryService
import io.eventstime.service.UserEventStandService
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
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
    private val authorizationService = mockk<AuthorizationService>()
    private val userEventStandService = mockk<UserEventStandService>()

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
        assertEquals(listOf(menuCategory.toResponse()), result)
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
        assertEquals(emptyList<MenuResponse>(), result)
        verify(exactly = 1) { productCategoryService.findMenuByEventId(eventId) }
    }

    @Test
    fun `Find all events by user`() {
        // GIVEN
        val userId = 1L

        every {
            authorizationService.getUser()
        } returns UserAuth(
            id = userId,
            firstName = "firstName",
            lastName = "lastName",
            email = "email@email.com",
            userGroupId = 1
        )
        every {
            userEventStandService.findAllEventsByUserId(userId)
        } returns emptyList()

        // WHEN
        val result = testObject.findAllEventsByUser()

        // THEN
        assertEquals(emptyList<EventResponse>(), result)
        verify(exactly = 1) { userEventStandService.findAllEventsByUserId(userId) }
    }

    @Test
    fun `Find all events by user returns list events`() {
        // GIVEN
        val userId = 1L
        val event = Event(
            id = 1,
            name = "test",
            address = "test",
            isPublic = false,
            programmedDateInitial = LocalDate.now(),
            programmedDateFinal = LocalDate.now()
        )
        val stand = Stand(
            id = 1,
            name = "event-1",
            event = event,
            isCashier = true,
            standCategory = StandCategory(
                id = 1,
                name = "stand-category",
                event = event
            )
        )

        val listEventsByUser = listOf(
            UserEventStandDto(
                id = 1,
                name = "teste 1",
                address = "teste 1",
                isPublic = true,
                programmedDateInitial = LocalDate.now(),
                programmedDateFinal = LocalDate.now(),
                stands = listOf(stand)
            ),
            UserEventStandDto(
                id = 2,
                name = "teste 2",
                address = "teste 2",
                isPublic = true,
                programmedDateInitial = LocalDate.now(),
                programmedDateFinal = LocalDate.now(),
                stands = listOf(stand)
            )
        )

        every {
            authorizationService.getUser()
        } returns UserAuth(
            id = userId,
            firstName = "firstName",
            lastName = "lastName",
            email = "email@email.com",
            userGroupId = 1
        )
        every {
            userEventStandService.findAllEventsByUserId(userId)
        } returns listEventsByUser

        // WHEN
        val result = testObject.findAllEventsByUser()

        // THEN
        assertEquals(listEventsByUser.toEventResponse(), result)
        verify(exactly = 1) { userEventStandService.findAllEventsByUserId(userId) }
    }

    @Test
    fun `Find all events by user returns empty list`() {
        // GIVEN
        val userId = 1L

        every {
            authorizationService.getUser()
        } returns UserAuth(
            id = userId,
            firstName = "firstName",
            lastName = "lastName",
            email = "email@email.com",
            userGroupId = 1
        )
        every {
            userEventStandService.findAllEventsByUserId(userId)
        } returns emptyList()

        // WHEN
        val result = testObject.findAllEventsByUser()

        // THEN
        assertEquals(emptyList<EventResponse>(), result)
        verify(exactly = 1) { userEventStandService.findAllEventsByUserId(userId) }
    }

    @Test
    fun `Find event by user returns event`() {
        // GIVEN
        val userId = 1L
        val event = Event(
            id = 1,
            name = "test",
            address = "test",
            isPublic = false,
            programmedDateInitial = LocalDate.now(),
            programmedDateFinal = LocalDate.now()
        )
        val stand = Stand(
            id = 1,
            name = "event-1",
            event = event,
            isCashier = true,
            standCategory = StandCategory(
                id = 1,
                name = "stand-category",
                event = event
            )
        )
        val eventByUser = UserEventStandDto(
            id = 1,
            name = "teste 1",
            address = "teste 1",
            isPublic = true,
            programmedDateInitial = LocalDate.now(),
            programmedDateFinal = LocalDate.now(),
            stands = listOf(stand)
        )

        every {
            authorizationService.getUser()
        } returns UserAuth(
            id = userId,
            firstName = "firstName",
            lastName = "lastName",
            email = "email@email.com",
            userGroupId = 1
        )
        every {
            userEventStandService.findEventByUserId(userId, event.id!!)
        } returns eventByUser

        // WHEN
        val result = testObject.findEventByUser(event.id!!)

        // THEN
        assertEquals(eventByUser.toEventResponse(), result)
        verify(exactly = 1) { userEventStandService.findEventByUserId(userId, event.id!!) }
    }

    @Test
    fun `Find event by user returns null`() {
        // GIVEN
        val eventId = 1L
        val userId = 1L

        every {
            authorizationService.getUser()
        } returns UserAuth(
            id = userId,
            firstName = "firstName",
            lastName = "lastName",
            email = "email@email.com",
            userGroupId = 1
        )
        every {
            userEventStandService.findEventByUserId(userId, eventId)
        } returns null

        // WHEN
        val result = testObject.findEventByUser(eventId)

        // THEN
        assertNull(result)
        verify(exactly = 1) { userEventStandService.findEventByUserId(userId, eventId) }
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
            assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        }

        @Test
        fun `Handle error generic exception`() {
            // GIVEN
            val exception = Exception("error")

            // WHEN
            val result = testObject.handleException(exception)

            // THEN
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.statusCode)
        }
    }
}
