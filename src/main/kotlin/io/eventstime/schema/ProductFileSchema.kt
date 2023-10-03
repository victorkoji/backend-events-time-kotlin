package io.eventstime.schema

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductFileSchema(
    val id: Long,
    val filename: String,

    @JsonProperty("media_type")
    val mediaType: String,
    val filepath: String
)
