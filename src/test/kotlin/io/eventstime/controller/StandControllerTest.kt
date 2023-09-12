package io.eventstime.controller

import io.eventstime.exception.CustomException
import io.eventstime.exception.EventErrorType
import io.eventstime.exception.StandCategoryErrorType
import io.eventstime.exception.StandErrorType
import io.eventstime.model.Event
import io.eventstime.model.Stand
import io.eventstime.model.StandCategory
import io.eventstime.schema.StandRequest
import io.eventstime.service.StandService
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
class StandControllerTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: StandController

    private val standService = mockk<StandService>()

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
    fun `Find all stands with list not empty`() {
        // GIVEN
        every {
            standService.findAll()
        } returns listOf(stand)

        // WHEN
        val result = testObject.findAllStands()

        // THEN
        Assertions.assertEquals(1, result.size)
        verify(exactly = 1) { standService.findAll() }
    }

    @Test
    fun `Find all stands with list empty`() {
        // GIVEN
        every {
            standService.findAll()
        } returns emptyList()

        // WHEN
        val result = testObject.findAllStands()

        // THEN
        Assertions.assertEquals(0, result.size)
        verify(exactly = 1) { standService.findAll() }
    }

    @Test
    fun `Create stand with success`() {
        // GIVEN
        val standCategoryRequest = StandRequest(
            name = stand.name,
            isCashier = stand.isCashier,
            eventId = stand.event?.id!!,
            standCategoryId = stand.standCategory?.id!!
        )

        every {
            standService.createStand(standCategoryRequest)
        } returns stand

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.createStand(standCategoryRequest)
        }

        // THEN
        verify(exactly = 1) { standService.createStand(standCategoryRequest) }
    }

    @Test
    fun `Update stand with success`() {
        // GIVEN
        val standCategoryRequest = StandRequest(
            name = stand.name,
            isCashier = stand.isCashier,
            eventId = stand.event?.id!!,
            standCategoryId = stand.standCategory?.id!!
        )

        every {
            standService.updateStand(stand.id!!, standCategoryRequest)
        } returns stand

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.updateStand(stand.id!!, standCategoryRequest)
        }

        // THEN
        verify(exactly = 1) { standService.updateStand(stand.id!!, standCategoryRequest) }
    }

    @Test
    fun `Delete stand with success`() {
        // GIVEN
        every {
            standService.deleteStand(stand.id!!)
        } just runs

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.deleteStand(stand.id!!)
        }

        // THEN
        verify(exactly = 1) { standService.deleteStand(stand.id!!) }
    }

    @Test
    fun `Find stand with success`() {
        // GIVEN
        every {
            standService.findById(stand.id!!)
        } returns stand

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.findStand(stand.id!!)
        }

        // THEN
        verify(exactly = 1) { standService.findById(stand.id!!) }
    }

    @Nested
    inner class HandleErrorTest {

        @Test
        fun `Handle error NOT_FOUND`() {
            // GIVEN
            val exceptionStandNotFound = CustomException(StandErrorType.STAND_NOT_FOUND)
            val exceptionStandCategoryNotFound = CustomException(StandCategoryErrorType.STAND_CATEGORY_NOT_FOUND)
            val exceptionEventNotFound = CustomException(EventErrorType.EVENT_NOT_FOUND)

            // WHEN
            val resultStandNotFound = testObject.handleException(exceptionStandNotFound)
            val resultStandCategoryNotFound = testObject.handleException(exceptionStandCategoryNotFound)
            val resultEventNotFound = testObject.handleException(exceptionEventNotFound)

            // THEN
            Assertions.assertEquals(HttpStatus.NOT_FOUND, resultStandNotFound.statusCode)
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
