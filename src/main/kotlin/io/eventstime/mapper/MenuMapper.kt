package io.eventstime.mapper

import io.eventstime.model.Product
import io.eventstime.model.MenuCategory
import io.eventstime.schema.MenuResponse
import io.eventstime.schema.ProductMenuResponse

fun MenuCategory.toResponse() = MenuResponse(
    id = id,
    name = name,
    products = products.toMenuResponse()
)

fun List<MenuCategory>.toResponse(): List<MenuResponse> {
    return if (this.isEmpty()) {
        emptyList()
    } else {
        this.map {
            it.toResponse()
        }
    }
}

fun Product.toMenuResponse() = ProductMenuResponse(
    id = id!!,
    name = name,
    price = price,
    customFormTemplate = customFormTemplate ?: "",
    stand = stand!!.toResponse(),
    productFile = productFile?.toResponse()
)

fun List<Product?>.toMenuResponse(): List<ProductMenuResponse> {
    return if (this.isEmpty()) {
        emptyList()
    } else {
        this.map { product ->
            product!!.toMenuResponse()
        }
    }
}
