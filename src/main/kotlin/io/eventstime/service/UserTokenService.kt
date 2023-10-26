package io.eventstime.service

import io.eventstime.exception.CustomException
import io.eventstime.exception.UserErrorType
import io.eventstime.model.User
import io.eventstime.model.UserToken
import io.eventstime.model.enum.AppClientEnum
import io.eventstime.repository.UserTokenRepository
import org.springframework.stereotype.Service

@Service
class UserTokenService(
    private val userTokenRepository: UserTokenRepository
) {

    fun updateRefreshToken(user: User, appClient: AppClientEnum, refreshToken: String) {
        userTokenRepository.findByUserAndAppClient(user, appClient)?.let { userToken ->
            userTokenRepository.save(userToken.copy(refreshToken = refreshToken))
        } ?: userTokenRepository.save(
            UserToken(
                user = user,
                refreshToken = refreshToken,
                appClient = appClient
            )
        )
    }

    fun validateRefreshToken(user: User, appClient: AppClientEnum, refreshToken: String): Boolean {
        return userTokenRepository.findByUserAndAppClient(user, appClient)?.let { userToken ->
            userToken.refreshToken == refreshToken
        } ?: false
    }

    fun insertTokenFcm(userId: Long, appClient: AppClientEnum, tokenFcm: String) {
        val userToken = userTokenRepository.findByUserIdAndAppClient(userId, appClient)
            ?: throw CustomException(UserErrorType.USER_TOKEN_NOT_FOUND)

        val updatedUserToken = userToken.copy(
            tokenFcm = tokenFcm
        )

        userTokenRepository.saveAndFlush(updatedUserToken)
    }

    fun deleteTokenFcm(userId: Long, appClient: AppClientEnum) {
        val userToken = userTokenRepository.findByUserIdAndAppClient(userId, appClient)
            ?: throw CustomException(UserErrorType.USER_TOKEN_NOT_FOUND)

        val updatedUserToken = userToken.copy(tokenFcm = null)

        userTokenRepository.saveAndFlush(updatedUserToken)
    }
}
