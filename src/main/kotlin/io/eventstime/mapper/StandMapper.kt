package io.eventstime.mapper

import io.eventstime.model.Stand
import io.eventstime.schema.StandResponse

fun Stand.toResponse() = StandResponse(
    id = id!!,
    name = name,
    isCashier = isCashier,
    eventId = event?.id!!,
    standCategoryId = standCategory?.id!!
)

fun List<Stand?>.toResponse(): List<StandResponse> {
    return if (this.isEmpty()) {
        emptyList()
    } else {
        this.map { stand ->
            StandResponse(
                id = stand?.id!!,
                name = stand.name,
                isCashier = stand.isCashier,
                eventId = stand.event?.id!!,
                standCategoryId = stand.standCategory?.id!!
            )
        }
    }
}