package io.eventstime.service

import io.eventstime.model.AppClient
import io.eventstime.model.User
import io.eventstime.model.UserToken
import io.eventstime.repository.UserTokenRepository
import org.springframework.stereotype.Service

@Service
class UserTokenService(
    private val userTokenRepository: UserTokenRepository
) {

    fun updateRefreshToken(user: User, appClient: AppClient, refreshToken: String) {
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

    fun validateRefreshToken(user: User, appClient: AppClient, refreshToken: String): Boolean {
        return userTokenRepository.findByUserAndAppClient(user, appClient)?.let { userToken ->
            userToken.refreshToken == refreshToken
        } ?: false
    }
}
