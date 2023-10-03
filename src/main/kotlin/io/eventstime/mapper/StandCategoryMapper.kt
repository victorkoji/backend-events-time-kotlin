package io.eventstime.mapper

import io.eventstime.model.StandCategory
import io.eventstime.schema.StandCategoryResponse

fun StandCategory.toResponse() = StandCategoryResponse(
    id = id!!,
    name = name,
    eventId = event?.id!!
)

fun List<StandCategory?>.toResponse(): List<StandCategoryResponse> {
    return if (this.isEmpty()) {
        emptyList()
    } else {
        this.map { it!!.toResponse() }
    }
}
