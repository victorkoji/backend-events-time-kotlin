package io.eventstime.service

import io.eventstime.dto.UserEventStandDto
import io.eventstime.exception.CustomException
import io.eventstime.exception.EventErrorType
import io.eventstime.model.*
import io.eventstime.repository.UserEventStandRepository
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import jakarta.persistence.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class UserEventStandServiceTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: UserEventStandService

    private val userEventStandRepository = mockk<UserEventStandRepository>()

    private val user = User(
        id = 1,
        firstName = "test",
        lastName = "test",
        birthDate = LocalDateTime.now(),
        email = "test@test.com",
        cellphone = "",
        password = "1234",
        userGroup = UserGroup(id = 1, name = "admin")
    )

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
    fun `Find all events by user id`() {
        // GIVEN
        val event2 = event.copy(id = 2, name = "event 2")
        val stand2 = stand.copy(event = event)
        every {
            userEventStandRepository.findAllByUserId(user.id!!)
        } returns listOf(
            UserEventStand(
                user = user,
                event = event,
                stand = stand,
                isResponsible = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                deletedAt = LocalDateTime.now()
            ),
            UserEventStand(
                user = user,
                event = event2,
                stand = stand2,
                isResponsible = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                deletedAt = LocalDateTime.now()
            )
        )

        val expectedResult = listOf(
            UserEventStandDto(
                id = event.id!!,
                name = event.name,
                address = event.address,
                isPublic = event.isPublic,
                programmedDateInitial = event.programmedDateInitial,
                programmedDateFinal = event.programmedDateFinal,
                stands = listOf(stand)
            ),
            UserEventStandDto(
                id = event2.id!!,
                name = event2.name,
                address = event2.address,
                isPublic = event2.isPublic,
                programmedDateInitial = event2.programmedDateInitial,
                programmedDateFinal = event2.programmedDateFinal,
                stands = listOf(stand2)
            )
        )

        // WHEN
        val result = testObject.findAllEventsByUserId(user.id!!)

        // THEN
        assertEquals(2, result.size)
        assertEquals(expectedResult, result)

        verify(exactly = 1) { userEventStandRepository.findAllByUserId(user.id!!) }
    }

    @Test
    fun `Find all events by user id returns empty list`() {
        // GIVEN
        every {
            userEventStandRepository.findAllByUserId(user.id!!)
        } returns emptyList()

        // WHEN
        val result = testObject.findAllEventsByUserId(user.id!!)

        // THEN
        assertEquals(0, result.size)
        verify(exactly = 1) { userEventStandRepository.findAllByUserId(user.id!!) }
    }

    @Test
    fun `Find event by user id`() {
        // GIVEN
        every {
            userEventStandRepository.findByUserIdAndEventId(user.id!!, event.id!!)
        } returns listOf(
            UserEventStand(
                user = user,
                event = event,
                stand = stand,
                isResponsible = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                deletedAt = LocalDateTime.now()
            )
        )

        val expectedResult = UserEventStandDto(
            id = event.id!!,
            name = event.name,
            address = event.address,
            isPublic = event.isPublic,
            programmedDateInitial = event.programmedDateInitial,
            programmedDateFinal = event.programmedDateFinal,
            stands = listOf(stand)
        )

        // WHEN
        val result = testObject.findEventByUserId(user.id!!, event.id!!)

        // THEN
        assertEquals(expectedResult, result)

        verify(exactly = 1) { userEventStandRepository.findByUserIdAndEventId(user.id!!, event.id!!) }
    }

    @Test
    fun `Find event by user id throws exception EVENT_NOT_FOUND`() {
        // GIVEN
        every {
            userEventStandRepository.findByUserIdAndEventId(user.id!!, event.id!!)
        } returns emptyList()

        // WHEN
        val exception = assertThrows<CustomException> {
            testObject.findEventByUserId(user.id!!, event.id!!)
        }

        // THEN
        assertEquals(EventErrorType.EVENT_NOT_FOUND.name, exception.message)

        verify(exactly = 1) { userEventStandRepository.findByUserIdAndEventId(user.id!!, event.id!!) }
    }
}
