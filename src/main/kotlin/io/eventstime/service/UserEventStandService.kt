package io.eventstime.service

import io.eventstime.dto.UserEventStandDto
import io.eventstime.exception.CustomException
import io.eventstime.exception.EventErrorType
import io.eventstime.model.Stand
import io.eventstime.model.UserEventStand
import io.eventstime.repository.UserEventStandRepository
import org.springframework.stereotype.Service

@Service
class UserEventStandService(
    private val userEventStandRepository: UserEventStandRepository
) {

    fun findAllEventsByUserId(userId: Long): List<UserEventStandDto?> {
        val userEventStands = userEventStandRepository.findAllByUserId(userId)
            .groupBy { it.event }

        return userEventStands.map { (_, userEventStand) ->
            userEventStand.groupByEventId()
        }
    }

    fun findEventByUserId(userId: Long, eventId: Long): UserEventStandDto? {
        val userEventStands = userEventStandRepository.findByUserIdAndEventId(userId, eventId)

        if (userEventStands.isEmpty()) throw CustomException(EventErrorType.EVENT_NOT_FOUND)

        return userEventStands.groupByEventId()
    }

    private fun List<UserEventStand>.groupByEventId(): UserEventStandDto {
        val event = this.first().event
        val stands = mutableListOf<Stand>()

        this.map {
            stands.add(
                Stand(
                    id = it.stand.id!!,
                    name = it.stand.name,
                    isCashier = it.stand.isCashier,
                    event = it.stand.event,
                    standCategory = it.stand.standCategory
                )
            )
        }

        return UserEventStandDto(
            id = event.id!!,
            name = event.name,
            address = event.address,
            isPublic = event.isPublic,
            programmedDateInitial = event.programmedDateInitial,
            programmedDateFinal = event.programmedDateFinal,
            stands = stands
        )
    }
}
