package io.eventstime.mapper

import io.eventstime.model.ProductFile
import io.eventstime.schema.ProductFileSchema

fun ProductFile.toResponse() = ProductFileSchema(
    id = id!!,
    filename = filename,
    mediaType = mediaType,
    filepath = filepath
)

fun List<ProductFile?>.toResponse(): List<ProductFileSchema> {
    return if (this.isEmpty()) {
        emptyList()
    } else {
        this.map { product ->
            product!!.toResponse()
        }
    }
}
