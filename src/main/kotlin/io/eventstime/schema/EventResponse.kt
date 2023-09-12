package io.eventstime.schema

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.LocalDate

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class EventResponse(
    val id: Long,
    val name: String,
    val address: String?,

    @JsonProperty("is_public")
    val isPublic: Boolean,

    @JsonProperty("programmed_date_initial")
    val programmedDateInitial: LocalDate,

    @JsonProperty("programmed_date_final")
    val programmedDateFinal: LocalDate
)
