package io.eventstime.schema

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductCategoryResponse(
    val id: Long,
    val name: String,

    @JsonProperty("event_id")
    val eventId: Long
)
