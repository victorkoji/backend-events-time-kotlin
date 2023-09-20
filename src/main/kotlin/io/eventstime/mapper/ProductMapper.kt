package io.eventstime.mapper

import io.eventstime.model.Product
import io.eventstime.schema.ProductResponse

fun Product.toResponse() = ProductResponse(
    id = id!!,
    name = name,
    price = price,
    customFormTemplate = customFormTemplate ?: "",
    productCategoryId = productCategory?.id!!,
    standId = stand?.id!!,
    productFileId = productFile?.id
)

fun List<Product?>.toResponse(): List<ProductResponse> {
    return if (this.isEmpty()) {
        emptyList()
    } else {
        this.map { product ->
            product!!.toResponse()
        }
    }
}
