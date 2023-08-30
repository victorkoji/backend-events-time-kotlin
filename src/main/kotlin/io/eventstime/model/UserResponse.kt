package io.eventstime.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

class UserResponse(
    val id: Long,
    @JsonProperty("first_name") val firstName: String,
    @JsonProperty("last_name") val lastName: String,
    @JsonProperty("birth_date") val birthDate: LocalDateTime,
    val email: String,
    val cellphone: String,
    @JsonProperty("token_fcm") val tokenFcm: String?,
    @JsonProperty("user_group_id") val userGroupId: Long
)
