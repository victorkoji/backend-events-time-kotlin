package io.eventstime.controller

import io.eventstime.exception.CustomException
import io.eventstime.exception.EventErrorType
import io.eventstime.exception.StandCategoryErrorType
import io.eventstime.model.Event
import io.eventstime.model.StandCategory
import io.eventstime.schema.StandCategoryRequest
import io.eventstime.service.StandCategoryService
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
class StandCategoryControllerTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: StandCategoryController

    private val standCategoryService = mockk<StandCategoryService>()

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
    fun `Find all stand categories with list not empty`() {
        // GIVEN
        every {
            standCategoryService.findAll()
        } returns listOf(standCategory)

        // WHEN
        val result = testObject.findAllStandCategories()

        // THEN
        Assertions.assertEquals(1, result.size)
        verify(exactly = 1) { standCategoryService.findAll() }
    }

    @Test
    fun `Find all stands categories with list empty`() {
        // GIVEN
        every {
            standCategoryService.findAll()
        } returns emptyList()

        // WHEN
        val result = testObject.findAllStandCategories()

        // THEN
        Assertions.assertEquals(0, result.size)
        verify(exactly = 1) { standCategoryService.findAll() }
    }

    @Test
    fun `Create stand category with success`() {
        // GIVEN
        val standCategoryRequest = StandCategoryRequest(
            name = standCategory.name,
            eventId = standCategory.event?.id!!
        )

        every {
            standCategoryService.createStandCategory(standCategoryRequest)
        } returns standCategory

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.createStandCategory(standCategoryRequest)
        }

        // THEN
        verify(exactly = 1) { standCategoryService.createStandCategory(standCategoryRequest) }
    }

    @Test
    fun `Update stand category with success`() {
        // GIVEN
        val standCategoryRequest = StandCategoryRequest(
            name = standCategory.name,
            eventId = standCategory.event?.id!!
        )

        every {
            standCategoryService.updateStandCategory(standCategory.id!!, standCategoryRequest)
        } returns standCategory

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.updateStandCategory(standCategory.id!!, standCategoryRequest)
        }

        // THEN
        verify(exactly = 1) { standCategoryService.updateStandCategory(standCategory.id!!, standCategoryRequest) }
    }

    @Test
    fun `Delete stand category with success`() {
        // GIVEN
        every {
            standCategoryService.deleteStandCategory(standCategory.id!!)
        } just runs

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.deleteStandCategory(standCategory.id!!)
        }

        // THEN
        verify(exactly = 1) { standCategoryService.deleteStandCategory(standCategory.id!!) }
    }

    @Test
    fun `Find stand category with success`() {
        // GIVEN
        every {
            standCategoryService.findById(standCategory.id!!)
        } returns standCategory

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.findStandCategory(standCategory.id!!)
        }

        // THEN
        verify(exactly = 1) { standCategoryService.findById(standCategory.id!!) }
    }

    @Nested
    inner class HandleErrorTest {

        @Test
        fun `Handle error NOT_FOUND`() {
            // GIVEN
            val exceptionStandCategoryNotFound = CustomException(StandCategoryErrorType.STAND_CATEGORY_NOT_FOUND)
            val exceptionEventNotFound = CustomException(EventErrorType.EVENT_NOT_FOUND)

            // WHEN
            val resultStandCategoryNotFound = testObject.handleException(exceptionStandCategoryNotFound)
            val resultEventNotFound = testObject.handleException(exceptionEventNotFound)

            // THEN
            Assertions.assertEquals(HttpStatus.NOT_FOUND, resultStandCategoryNotFound.statusCode)
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
