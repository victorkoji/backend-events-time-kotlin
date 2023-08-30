package io.eventstime.model.dto

import org.springframework.web.server.ResponseStatusException

/**
 * This file contains all outgoing DTOs.
 * [ApiException] is used to easily throw exceptions.
 */
class ApiException(code: Int, message: String) : ResponseStatusException(code, message, null)

data class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String
)

data class ItemDto(
    val id: Long,
    val name: String,
    val count: Int,
    val note: String?
)
