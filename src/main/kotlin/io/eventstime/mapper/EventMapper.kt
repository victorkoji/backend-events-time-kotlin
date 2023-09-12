package io.eventstime.mapper

import io.eventstime.model.Event
import io.eventstime.schema.EventResponse

fun Event.toResponse() = EventResponse(
    id = id!!,
    name = name,
    address = address,
    isPublic = isPublic,
    programmedDateInitial = programmedDateInitial,
    programmedDateFinal = programmedDateFinal
)

fun List<Event?>.toResponse(): List<EventResponse> {
    return if (this.isEmpty()) {
        emptyList()
    } else {
        this.map { event ->
            EventResponse(
                id = event?.id!!,
                name = event.name,
                address = event.address,
                isPublic = event.isPublic,
                programmedDateInitial = event.programmedDateInitial,
                programmedDateFinal = event.programmedDateFinal
            )
        }
    }
}
