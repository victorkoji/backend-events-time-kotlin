package io.eventstime.model

import io.eventstime.model.enum.AppClientEnum

data class UserAuth(
    val id: Long,
    val firstName: String?,
    val lastName: String?,
    val email: String,
    val userGroupId: Long,
    val appClient: AppClientEnum
)
