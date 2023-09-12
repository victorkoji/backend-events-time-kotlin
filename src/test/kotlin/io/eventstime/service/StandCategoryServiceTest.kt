package io.eventstime.service

import io.eventstime.exception.CustomException
import io.eventstime.exception.EventErrorType
import io.eventstime.exception.StandCategoryErrorType
import io.eventstime.model.Event
import io.eventstime.model.StandCategory
import io.eventstime.repository.StandCategoryRepository
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
class StandCategoryServiceTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: StandCategoryService

    private val standCategoryRepository = mockk<StandCategoryRepository>()
    private val eventService = mockk<EventService>()

    private val standCategory = StandCategory(
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
    fun `Find all stand categories list not empty`() {
        // GIVEN
        every {
            standCategoryRepository.findAll()
        } returns listOf(standCategory)

        // WHEN
        val result = testObject.findAll()

        // THEN
        assertEquals(1, result.size)
        assertEquals(1, standCategory.id)

        verify(exactly = 1) { standCategoryRepository.findAll() }
    }

    @Test
    fun `Find all stand categories list empty`() {
        // GIVEN
        every {
            standCategoryRepository.findAll()
        } returns emptyList()

        // WHEN
        val result = testObject.findAll()

        // THEN
        assertEquals(0, result.size)
        verify(exactly = 1) { standCategoryRepository.findAll() }
    }

    @Test
    fun `Find stand category by id return object stand category`() {
        // GIVEN
        every {
            standCategoryRepository.findById(standCategory.id!!)
        } returns Optional.of(standCategory)

        // WHEN
        val result = testObject.findById(standCategory.id!!)

        // THEN
        assertNotNull(result)

        verify(exactly = 1) { standCategoryRepository.findById(standCategory.id!!) }
    }

    @Test
    fun `Find stand category by id return null`() {
        // GIVEN
        val standCategoryId = 1L

        every {
            standCategoryRepository.findById(standCategoryId)
        } returns Optional.empty()

        // WHEN
        val result = testObject.findById(standCategoryId)

        // THEN
        assertNull(result)
        verify(exactly = 1) { standCategoryRepository.findById(standCategoryId) }
    }

    @Test
    fun `Create stand category with success`() {
        // GIVEN
        val standCategoryRequest = StandCategoryRequest(
            name = standCategory.name,
            eventId = standCategory.event?.id!!
        )

        every {
            eventService.findById(standCategory.event?.id!!)
        } returns standCategory.event

        every {
            standCategoryRepository.saveAndFlush(standCategory.copy(id = null))
        } returns standCategory

        // WHEN
        val result = testObject.createStandCategory(standCategoryRequest)

        // THEN
        assertNotNull(result)
        verify(exactly = 1) { standCategoryRepository.saveAndFlush(standCategory.copy(id = null)) }
    }

    @Test
    fun `Create stand category with event not found error `() {
        // GIVEN
        val standCategoryRequest = StandCategoryRequest(
            name = standCategory.name,
            eventId = standCategory.event?.id!!
        )

        every {
            eventService.findById(standCategory.event?.id!!)
        } returns null

        // WHEN
        val result = assertThrows<CustomException> { testObject.createStandCategory(standCategoryRequest) }

        // THEN
        assertEquals(EventErrorType.EVENT_NOT_FOUND.name, result.message)
        verify(exactly = 0) { standCategoryRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Create stand category with error to save`() {
        // GIVEN
        val standCategoryRequest = StandCategoryRequest(
            name = standCategory.name,
            eventId = standCategory.event?.id!!
        )

        every {
            eventService.findById(standCategory.event?.id!!)
        } returns standCategory.event

        every {
            standCategoryRepository.saveAndFlush(any())
        } throws Exception("Failure to save")

        // WHEN
        val result = assertThrows<Exception> { testObject.createStandCategory(standCategoryRequest) }

        // THEN
        assertEquals("Failure to save", result.message)
        verify(exactly = 1) { standCategoryRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Update stand category with success`() {
        // GIVEN
        val standCategoryRequest = StandCategoryRequest(
            name = standCategory.name,
            eventId = standCategory.event?.id!!
        )

        every {
            eventService.findById(standCategory.event?.id!!)
        } returns standCategory.event

        every {
            standCategoryRepository.findById(standCategory.id!!)
        } returns Optional.of(standCategory)

        every {
            standCategoryRepository.saveAndFlush(standCategory)
        } returns standCategory

        // WHEN
        val result = testObject.updateStandCategory(standCategory.id!!, standCategoryRequest)

        // THEN
        assertNotNull(result)
        verify(exactly = 1) { standCategoryRepository.saveAndFlush(standCategory) }
    }

    @Test
    fun `Update stand category with error stand category not found`() {
        // GIVEN
        val standCategoryRequest = StandCategoryRequest(
            name = standCategory.name,
            eventId = standCategory.event?.id!!
        )

        every {
            eventService.findById(standCategory.event?.id!!)
        } returns standCategory.event

        every {
            standCategoryRepository.findById(standCategory.id!!)
        } returns Optional.empty()

        every {
            standCategoryRepository.saveAndFlush(standCategory)
        } returns standCategory

        // WHEN
        val result = assertThrows<CustomException> {
            testObject.updateStandCategory(standCategory.id!!, standCategoryRequest)
        }

        // THEN
        assertEquals(StandCategoryErrorType.STAND_CATEGORY_NOT_FOUND.name, result.message)
        verify(exactly = 0) { standCategoryRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Update stand category with event not found error`() {
        // GIVEN
        val standCategoryRequest = StandCategoryRequest(
            name = standCategory.name,
            eventId = standCategory.event?.id!!
        )

        every {
            eventService.findById(standCategory.event?.id!!)
        } returns null

        every {
            standCategoryRepository.findById(standCategory.id!!)
        } returns Optional.empty()

        every {
            standCategoryRepository.saveAndFlush(standCategory)
        } returns standCategory

        // WHEN
        val result = assertThrows<CustomException> {
            testObject.updateStandCategory(standCategory.id!!, standCategoryRequest)
        }

        // THEN
        assertEquals(EventErrorType.EVENT_NOT_FOUND.name, result.message)
        verify(exactly = 0) { standCategoryRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Delete stand category with success`() {
        // GIVEN
        every {
            standCategoryRepository.findById(standCategory.id!!)
        } returns Optional.of(standCategory)

        every {
            standCategoryRepository.delete(standCategory)
        } just runs

        // WHEN
        val result = testObject.deleteStandCategory(standCategory.id!!)

        // THEN
        assertNotNull(result)
        verify(exactly = 1) { standCategoryRepository.delete(standCategory) }
    }

    @Test
    fun `Delete stand category with error stand category not found`() {
        // GIVEN
        every {
            standCategoryRepository.findById(standCategory.id!!)
        } returns Optional.empty()

        every {
            standCategoryRepository.delete(standCategory)
        } just runs

        // WHEN
        val result = assertThrows<CustomException> { testObject.deleteStandCategory(standCategory.id!!) }

        // THEN
        assertEquals(StandCategoryErrorType.STAND_CATEGORY_NOT_FOUND.name, result.message)
        verify(exactly = 0) { standCategoryRepository.delete(any()) }
    }
}
