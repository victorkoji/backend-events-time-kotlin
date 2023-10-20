package io.eventstime.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.eventstime.model.Stand
import java.time.LocalDate

data class UserEventStandDto(
    val id: Long,
    val name: String,
    val address: String?,

    @JsonProperty("is_public")
    val isPublic: Boolean,

    @JsonProperty("programmed_date_initial")
    val programmedDateInitial: LocalDate,

    @JsonProperty("programmed_date_final")
    val programmedDateFinal: LocalDate,

    val stands: List<Stand>? = null
)
