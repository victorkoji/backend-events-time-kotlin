package io.eventstime.schema

import com.fasterxml.jackson.annotation.JsonProperty

data class StandRequest(
    val name: String,

    @JsonProperty("is_cashier")
    val isCashier: Boolean,

    @JsonProperty("event_id")
    val eventId: Long,

    @JsonProperty("stand_category_id")
    val standCategoryId: Long
)