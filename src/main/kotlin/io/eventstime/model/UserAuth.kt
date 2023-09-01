package io.eventstime.model

data class UserAuth(
    val id: Long,
    val firstName: String?,
    val lastName: String?,
    val email: String,
    val tokenFcm: String?,
    val userGroupId: Long
)
