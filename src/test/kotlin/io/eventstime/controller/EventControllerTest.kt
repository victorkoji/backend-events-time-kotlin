package io.eventstime.controller

import io.eventstime.exception.CustomException
import io.eventstime.exception.EventErrorType
import io.eventstime.model.Event
import io.eventstime.schema.EventRequest
import io.eventstime.service.EventService
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
class EventControllerTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: EventController

    private val eventService = mockk<EventService>()

    private val event = Event(
        id = 1,
        name = "event-1",
        address = "",
        isPublic = false,
        programmedDateInitial = LocalDate.now(),
        programmedDateFinal = LocalDate.now()
    )

    @Test
    fun `Find all event with list not empty`() {
        // GIVEN
        every {
            eventService.findAll()
        } returns listOf(event)

        // WHEN
        val result = testObject.findAllEvents()

        // THEN
        Assertions.assertEquals(1, result.size)
        verify(exactly = 1) { eventService.findAll() }
    }

    @Test
    fun `Find all event with list empty`() {
        // GIVEN
        every {
            eventService.findAll()
        } returns emptyList()

        // WHEN
        val result = testObject.findAllEvents()

        // THEN
        Assertions.assertEquals(0, result.size)
        verify(exactly = 1) { eventService.findAll() }
    }

    @Test
    fun `Create event with success`() {
        // GIVEN
        val eventRequest = EventRequest(
            name = "event 1",
            address = "address",
            isPublic = false,
            programmedDateInitial = LocalDate.now(),
            programmedDateFinal = LocalDate.now()
        )

        every {
            eventService.createEvent(eventRequest)
        } returns event

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.createEvent(eventRequest)
        }

        // THEN
        verify(exactly = 1) { eventService.createEvent(eventRequest) }
    }

    @Test
    fun `Update event with success`() {
        // GIVEN
        val eventRequest = EventRequest(
            name = "event 1",
            address = "address",
            isPublic = false,
            programmedDateInitial = LocalDate.now(),
            programmedDateFinal = LocalDate.now()
        )

        every {
            eventService.updateEvent(event.id!!, eventRequest)
        } returns event

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.updateEvent(event.id!!, eventRequest)
        }

        // THEN
        verify(exactly = 1) { eventService.updateEvent(event.id!!, eventRequest) }
    }

    @Test
    fun `Delete event with success`() {
        // GIVEN
        every {
            eventService.deleteEvent(event.id!!)
        } just runs

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.deleteEvent(event.id!!)
        }

        // THEN
        verify(exactly = 1) { eventService.deleteEvent(event.id!!) }
    }

    @Test
    fun `Find event with success`() {
        // GIVEN
        every {
            eventService.findById(event.id!!)
        } returns event

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.findEvent(event.id!!)
        }

        // THEN
        verify(exactly = 1) { eventService.findById(event.id!!) }
    }

    @Nested
    inner class HandleErrorTest {

        @Test
        fun `Handle error NOT_FOUND`() {
            // GIVEN
            val exceptionEventNotFound = CustomException(EventErrorType.EVENT_NOT_FOUND)

            // WHEN
            val resultEventNotFound = testObject.handleException(exceptionEventNotFound)

            // THEN
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
