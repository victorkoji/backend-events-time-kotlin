package io.eventstime.schema

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginRequest(
    val email: String,
    val password: String
)

data class RefreshTokenRequest(
    @JsonProperty("refresh_token")
    val refreshToken: String
)
