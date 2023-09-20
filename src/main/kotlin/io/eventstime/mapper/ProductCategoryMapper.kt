package io.eventstime.mapper

import io.eventstime.model.ProductCategory
import io.eventstime.schema.ProductCategoryResponse

fun ProductCategory.toResponse() = ProductCategoryResponse(
    id = id!!,
    name = name,
    eventId = event?.id!!
)

fun List<ProductCategory?>.toResponse(): List<ProductCategoryResponse> {
    return if (this.isEmpty()) {
        emptyList()
    } else {
        this.map { event ->
            ProductCategoryResponse(
                id = event?.id!!,
                name = event.name,
                eventId = event.id
            )
        }
    }
}
