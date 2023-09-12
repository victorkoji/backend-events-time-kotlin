package io.eventstime.service

import io.eventstime.exception.CustomException
import io.eventstime.exception.EventErrorType
import io.eventstime.model.Event
import io.eventstime.repository.EventRepository
import io.eventstime.schema.EventRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class EventService(
    private val eventRepository: EventRepository
) {
    fun findAll(): List<Event?> {
        return eventRepository.findAll()
    }

    fun findById(eventId: Long): Event? {
        return eventRepository.findByIdOrNull(eventId)
    }

    fun createEvent(event: EventRequest): Event {
        return eventRepository.saveAndFlush(
            Event(
                name = event.name,
                address = event.address,
                isPublic = event.isPublic,
                programmedDateInitial = event.programmedDateInitial,
                programmedDateFinal = event.programmedDateFinal
            )
        )
    }

    fun updateEvent(eventId: Long, event: EventRequest): Event {
        val updatedEvent = findById(eventId)?.copy(
            id = eventId,
            name = event.name,
            address = event.address,
            isPublic = event.isPublic,
            programmedDateInitial = event.programmedDateInitial,
            programmedDateFinal = event.programmedDateFinal
        ) ?: throw CustomException(EventErrorType.EVENT_NOT_FOUND)

        return eventRepository.saveAndFlush(updatedEvent)
    }

    fun deleteEvent(eventId: Long) {
        findById(eventId)?.let { event ->
            eventRepository.delete(event)
        } ?: throw CustomException(EventErrorType.EVENT_NOT_FOUND)
    }
}
