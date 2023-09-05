package io.eventstime.schema

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class EventRequest(
    val name: String,
    val address: String?,

    @JsonProperty("is_public")
    val isPublic: Boolean,

    @JsonProperty("programmed_date_initial")
    val programmedDateInitial: LocalDate,

    @JsonProperty("programmed_date_final")
    val programmedDateFinal: LocalDate
)
