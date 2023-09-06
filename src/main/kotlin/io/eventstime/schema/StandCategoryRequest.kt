package io.eventstime.schema

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*

data class StandCategoryRequest(
    val name: String,

    @JsonProperty("event_id")
    val eventId: Long
)
