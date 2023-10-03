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
        this.map { it!!.toResponse() }
    }
}
