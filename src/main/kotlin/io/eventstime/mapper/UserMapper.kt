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
    tokenFcm = tokenFcm,
    userGroupId = userGroup!!.id
)

fun List<User?>.toResponse(): List<UserResponse> {
    return if (this.isEmpty()) {
        emptyList()
    } else {
        this.map { user ->
            UserResponse(
                id = user?.id!!,
                firstName = user.firstName,
                lastName = user.lastName,
                birthDate = user.birthDate,
                email = user.email,
                cellphone = user.cellphone,
                tokenFcm = user.tokenFcm,
                userGroupId = user.userGroup!!.id
            )
        }
    }
}
