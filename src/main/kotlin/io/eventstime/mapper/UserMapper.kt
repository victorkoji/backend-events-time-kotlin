package io.eventstime.mapper

import io.eventstime.model.User
import io.eventstime.schema.UserResponse

fun User.toResponse() = UserResponse(
    id = id!!,
    firstName = firstName,
    lastName = lastName,
    birthDate = birthDate,
    email = email,
    cellphone = cellphone,
    userGroupId = userGroup!!.id
)

fun List<User?>.toResponse(): List<UserResponse> {
    return if (this.isEmpty()) {
        emptyList()
    } else {
        this.map { it!!.toResponse() }
    }
}
