package io.eventstime.mapper

import io.eventstime.dto.UserEventStandDto
import io.eventstime.schema.EventResponse

fun UserEventStandDto.toEventResponse() = EventResponse(
    id = id,
    name = name,
    address = address,
    isPublic = isPublic,
    programmedDateInitial = programmedDateInitial,
    programmedDateFinal = programmedDateFinal,
    stands = stands?.toResponse()
)

fun List<UserEventStandDto?>.toEventResponse(): List<EventResponse> {
    return if (this.isEmpty()) {
        emptyList()
    } else {
        this.map { it!!.toEventResponse() }
    }
}
