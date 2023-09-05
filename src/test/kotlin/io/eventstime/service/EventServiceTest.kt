package io.eventstime.service

import io.eventstime.exception.CustomException
import io.eventstime.exception.EventErrorType
import io.eventstime.model.Event
import io.eventstime.repository.EventRepository
import io.eventstime.schema.EventRequest
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
class EventServiceTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: EventService

    private val eventRepository = mockk<EventRepository>()

    private val event = Event(
        id = 1,
        name = "event-1",
        address = "",
        isPublic = false,
        programmedDateInitial = LocalDate.now(),
        programmedDateFinal = LocalDate.now()
    )

    @Test
    fun `Find all events list not empty`() {
        // GIVEN
        every {
            eventRepository.findAll()
        } returns listOf(
            event
        )

        // WHEN
        val result = testObject.findAll()

        // THEN
        assertEquals(1, result.size)
        assertEquals(1, event.id)

        verify(exactly = 1) { eventRepository.findAll() }
    }

    @Test
    fun `Find all events list empty`() {
        // GIVEN
        every {
            eventRepository.findAll()
        } returns emptyList()

        // WHEN
        val result = testObject.findAll()

        // THEN
        assertEquals(0, result.size)
        verify(exactly = 1) { eventRepository.findAll() }
    }

    @Test
    fun `Find event by id return object event`() {
        // GIVEN
        every {
            eventRepository.findById(event.id!!)
        } returns Optional.of(event)

        // WHEN
        val result = testObject.findById(event.id!!)

        // THEN
        assertNotNull(result)

        verify(exactly = 1) { eventRepository.findById(event.id!!) }
    }

    @Test
    fun `Find event by id return null`() {
        // GIVEN
        val eventId = 1L

        every {
            eventRepository.findById(eventId)
        } returns Optional.empty()

        // WHEN
        val result = testObject.findById(eventId)

        // THEN
        assertNull(result)
        verify(exactly = 1) { eventRepository.findById(eventId) }
    }

    @Test
    fun `Create event with success`() {
        // GIVEN
        val eventRequest = EventRequest(
            name = event.name,
            address = event.address,
            isPublic = event.isPublic,
            programmedDateInitial = event.programmedDateInitial,
            programmedDateFinal = event.programmedDateFinal
        )

        every {
            eventRepository.saveAndFlush(event.copy(id = null))
        } returns event

        // WHEN
        val result = testObject.createEvent(eventRequest)

        // THEN
        assertNotNull(result)
        verify(exactly = 1) { eventRepository.saveAndFlush(event.copy(id = null)) }
    }

    @Test
    fun `Create event with error`() {
        // GIVEN
        val eventRequest = EventRequest(
            name = event.name,
            address = event.address,
            isPublic = event.isPublic,
            programmedDateInitial = event.programmedDateInitial,
            programmedDateFinal = event.programmedDateFinal
        )

        every {
            eventRepository.saveAndFlush(event)
        } throws Exception("Failure to save")

        // WHEN
        assertThrows<Exception> { testObject.createEvent(eventRequest) }

        // THEN
        verify(exactly = 1) { eventRepository.saveAndFlush(event.copy(id = null)) }
    }

    @Test
    fun `Update event with success`() {
        // GIVEN
        val eventRequest = EventRequest(
            name = event.name,
            address = event.address,
            isPublic = event.isPublic,
            programmedDateInitial = event.programmedDateInitial,
            programmedDateFinal = event.programmedDateFinal
        )

        every {
            eventRepository.findById(event.id!!)
        } returns Optional.of(event)

        every {
            eventRepository.saveAndFlush(event)
        } returns event

        // WHEN
        val result = testObject.updateEvent(event.id!!, eventRequest)

        // THEN
        assertNotNull(result)
        verify(exactly = 1) { eventRepository.saveAndFlush(event) }
    }

    @Test
    fun `Update event with error event not found`() {
        // GIVEN
        val eventRequest = EventRequest(
            name = event.name,
            address = event.address,
            isPublic = event.isPublic,
            programmedDateInitial = event.programmedDateInitial,
            programmedDateFinal = event.programmedDateFinal
        )

        every {
            eventRepository.findById(event.id!!)
        } returns Optional.empty()

        every {
            eventRepository.saveAndFlush(event)
        } returns event

        // WHEN
        val result = assertThrows<CustomException> { testObject.updateEvent(event.id!!, eventRequest) }

        // THEN
        assertEquals(EventErrorType.EVENT_NOT_FOUND.name, result.message)
        verify(exactly = 0) { eventRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Delete event with success`() {
        // GIVEN
        every {
            eventRepository.findById(event.id!!)
        } returns Optional.of(event)

        every {
            eventRepository.delete(event)
        } just runs

        // WHEN
        val result = testObject.deleteEvent(event.id!!)

        // THEN
        assertNotNull(result)
        verify(exactly = 1) { eventRepository.delete(event) }
    }

    @Test
    fun `Delete event with error event not found`() {
        // GIVEN
        every {
            eventRepository.findById(event.id!!)
        } returns Optional.empty()

        every {
            eventRepository.delete(event)
        } just runs

        // WHEN
        val result = assertThrows<CustomException> { testObject.deleteEvent(event.id!!) }

        // THEN
        assertEquals(EventErrorType.EVENT_NOT_FOUND.name, result.message)
        verify(exactly = 0) { eventRepository.delete(any()) }
    }
}
