package io.eventstime.auth

import io.eventstime.exception.AuthErrorType
import io.eventstime.exception.CustomException
import io.eventstime.model.UserAuth
import io.eventstime.utils.HashUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthorizationService(
    private val hashUtils: HashUtils
) {

    private fun getToken() = SecurityContextHolder.getContext().authentication.principal

    fun getUser(): UserAuth {
        return when (val userToken = getToken()) {
            is UserAuth ->
                UserAuth(
                    id = userToken.id,
                    firstName = userToken.firstName,
                    lastName = userToken.lastName,
                    email = userToken.email,
                    userGroupId = userToken.userGroupId
                )

            else -> throw CustomException(AuthErrorType.UNAUTHORIZED)
        }
    }

    fun checkIsValidPassword(password: String, passwordToCompare: String): Boolean {
        return hashUtils.checkBcrypt(password, passwordToCompare)
    }
}
