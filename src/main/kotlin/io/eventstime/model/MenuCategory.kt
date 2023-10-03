package io.eventstime.model

import com.fasterxml.jackson.annotation.JsonProperty

data class MenuCategory(
    val id: Long,
    val name: String,

    @JsonProperty("event_id")
    val eventId: Long,

    val products: List<Product?>
)
