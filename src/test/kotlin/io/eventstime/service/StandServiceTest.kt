package io.eventstime.service

import io.eventstime.exception.CustomException
import io.eventstime.exception.EventErrorType
import io.eventstime.exception.StandCategoryErrorType
import io.eventstime.exception.StandErrorType
import io.eventstime.model.Event
import io.eventstime.model.Stand
import io.eventstime.model.StandCategory
import io.eventstime.repository.StandRepository
import io.eventstime.schema.StandRequest
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
class StandServiceTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: StandService

    private val standRepository = mockk<StandRepository>()
    private val eventService = mockk<EventService>()
    private val standCategoryService = mockk<StandCategoryService>()

    private val event = Event(
        id = 1,
        name = "test",
        address = "test",
        isPublic = false,
        programmedDateInitial = LocalDate.now(),
        programmedDateFinal = LocalDate.now()
    )
    private val stand = Stand(
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

    @Test
    fun `Find all stands list not empty`() {
        // GIVEN
        every {
            standRepository.findAll()
        } returns listOf(stand)

        // WHEN
        val result = testObject.findAll()

        // THEN
        assertEquals(1, result.size)
        assertEquals(1, stand.id)

        verify(exactly = 1) { standRepository.findAll() }
    }

    @Test
    fun `Find all stands list empty`() {
        // GIVEN
        every {
            standRepository.findAll()
        } returns emptyList()

        // WHEN
        val result = testObject.findAll()

        // THEN
        assertEquals(0, result.size)
        verify(exactly = 1) { standRepository.findAll() }
    }

    @Test
    fun `Find stand by id return object stand`() {
        // GIVEN
        every {
            standRepository.findById(stand.id!!)
        } returns Optional.of(stand)

        // WHEN
        val result = testObject.findById(stand.id!!)

        // THEN
        assertNotNull(result)

        verify(exactly = 1) { standRepository.findById(stand.id!!) }
    }

    @Test
    fun `Find stand by id return null`() {
        // GIVEN
        val standCategoryId = 1L

        every {
            standRepository.findById(standCategoryId)
        } returns Optional.empty()

        // WHEN
        val result = testObject.findById(standCategoryId)

        // THEN
        assertNull(result)
        verify(exactly = 1) { standRepository.findById(standCategoryId) }
    }

    @Test
    fun `Create stand with success`() {
        // GIVEN
        val standRequest = StandRequest(
            name = stand.name,
            isCashier = stand.isCashier,
            eventId = stand.event?.id!!,
            standCategoryId = stand.standCategory?.id!!
        )

        every {
            eventService.findById(stand.event?.id!!)
        } returns stand.event

        every {
            standCategoryService.findById(stand.standCategory?.id!!)
        } returns stand.standCategory

        every {
            standRepository.saveAndFlush(stand.copy(id = null))
        } returns stand

        // WHEN
        val result = testObject.createStand(standRequest)

        // THEN
        assertNotNull(result)
        verify(exactly = 1) { standRepository.saveAndFlush(stand.copy(id = null)) }
    }

    @Test
    fun `Create stand with event not found error `() {
        // GIVEN
        val standRequest = StandRequest(
            name = stand.name,
            isCashier = stand.isCashier,
            eventId = stand.event?.id!!,
            standCategoryId = stand.standCategory?.id!!
        )

        every {
            eventService.findById(stand.event?.id!!)
        } returns null

        // WHEN
        val result = assertThrows<CustomException> { testObject.createStand(standRequest) }

        // THEN
        assertEquals(EventErrorType.EVENT_NOT_FOUND.name, result.message)
        verify(exactly = 0) { standRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Create stand with stand category not found error `() {
        // GIVEN
        val standRequest = StandRequest(
            name = stand.name,
            isCashier = stand.isCashier,
            eventId = stand.event?.id!!,
            standCategoryId = stand.standCategory?.id!!
        )

        every {
            eventService.findById(stand.event?.id!!)
        } returns event

        every {
            standCategoryService.findById(stand.standCategory?.id!!)
        } returns null

        // WHEN
        val result = assertThrows<CustomException> { testObject.createStand(standRequest) }

        // THEN
        assertEquals(StandCategoryErrorType.STAND_CATEGORY_NOT_FOUND.name, result.message)
        verify(exactly = 0) { standRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Create stand with error to save`() {
        // GIVEN
        val standRequest = StandRequest(
            name = stand.name,
            isCashier = stand.isCashier,
            eventId = stand.event?.id!!,
            standCategoryId = stand.standCategory?.id!!
        )

        every {
            eventService.findById(stand.event?.id!!)
        } returns stand.event

        every {
            standCategoryService.findById(stand.standCategory?.id!!)
        } returns stand.standCategory

        every {
            standRepository.saveAndFlush(any())
        } throws Exception("Failure to save")

        // WHEN
        val result = assertThrows<Exception> { testObject.createStand(standRequest) }

        // THEN
        assertEquals("Failure to save", result.message)
        verify(exactly = 1) { standRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Update stand with success`() {
        // GIVEN
        val standRequest = StandRequest(
            name = stand.name,
            isCashier = stand.isCashier,
            eventId = stand.event?.id!!,
            standCategoryId = stand.standCategory?.id!!
        )

        every {
            eventService.findById(stand.event?.id!!)
        } returns stand.event

        every {
            standCategoryService.findById(stand.standCategory?.id!!)
        } returns stand.standCategory

        every {
            standRepository.findById(stand.id!!)
        } returns Optional.of(stand)

        every {
            standRepository.saveAndFlush(stand)
        } returns stand

        // WHEN
        val result = testObject.updateStand(stand.id!!, standRequest)

        // THEN
        assertNotNull(result)
        verify(exactly = 1) { standRepository.saveAndFlush(stand) }
    }

    @Test
    fun `Update stand with error stand not found`() {
        // GIVEN
        val standRequest = StandRequest(
            name = stand.name,
            isCashier = stand.isCashier,
            eventId = stand.event?.id!!,
            standCategoryId = stand.standCategory?.id!!
        )

        every {
            eventService.findById(stand.event?.id!!)
        } returns stand.event

        every {
            standCategoryService.findById(stand.standCategory?.id!!)
        } returns stand.standCategory

        every {
            standRepository.findById(stand.id!!)
        } returns Optional.empty()

        every {
            standRepository.saveAndFlush(stand)
        } returns stand

        // WHEN
        val result = assertThrows<CustomException> {
            testObject.updateStand(stand.id!!, standRequest)
        }

        // THEN
        assertEquals(StandErrorType.STAND_NOT_FOUND.name, result.message)
        verify(exactly = 0) { standRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Update stand with event not found error`() {
        // GIVEN
        val standRequest = StandRequest(
            name = stand.name,
            isCashier = stand.isCashier,
            eventId = stand.event?.id!!,
            standCategoryId = stand.standCategory?.id!!
        )

        every {
            eventService.findById(stand.event?.id!!)
        } returns null

        every {
            standRepository.findById(stand.id!!)
        } returns Optional.empty()

        every {
            standRepository.saveAndFlush(stand)
        } returns stand

        // WHEN
        val result = assertThrows<CustomException> {
            testObject.updateStand(stand.id!!, standRequest)
        }

        // THEN
        assertEquals(EventErrorType.EVENT_NOT_FOUND.name, result.message)
        verify(exactly = 0) { standRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Update stand with stand category not found error`() {
        // GIVEN
        val standRequest = StandRequest(
            name = stand.name,
            isCashier = stand.isCashier,
            eventId = stand.event?.id!!,
            standCategoryId = stand.standCategory?.id!!
        )

        every {
            eventService.findById(stand.event?.id!!)
        } returns event

        every {
            standCategoryService.findById(stand.standCategory?.id!!)
        } returns null

        every {
            standRepository.findById(stand.id!!)
        } returns Optional.empty()

        every {
            standRepository.saveAndFlush(stand)
        } returns stand

        // WHEN
        val result = assertThrows<CustomException> {
            testObject.updateStand(stand.id!!, standRequest)
        }

        // THEN
        assertEquals(StandCategoryErrorType.STAND_CATEGORY_NOT_FOUND.name, result.message)
        verify(exactly = 0) { standRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Delete stand with success`() {
        // GIVEN
        every {
            standRepository.findById(stand.id!!)
        } returns Optional.of(stand)

        every {
            standRepository.delete(stand)
        } just runs

        // WHEN
        val result = testObject.deleteStand(stand.id!!)

        // THEN
        assertNotNull(result)
        verify(exactly = 1) { standRepository.delete(stand) }
    }

    @Test
    fun `Delete stand with error stand not found`() {
        // GIVEN
        every {
            standRepository.findById(stand.id!!)
        } returns Optional.empty()

        every {
            standRepository.delete(stand)
        } just runs

        // WHEN
        val result = assertThrows<CustomException> { testObject.deleteStand(stand.id!!) }

        // THEN
        assertEquals(StandErrorType.STAND_NOT_FOUND.name, result.message)
        verify(exactly = 0) { standRepository.delete(any()) }
    }
}
