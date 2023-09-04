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
        val userToken: UserToken? = userTokenRepository.findByUserAndAppClient(user, appClient)

        if (userToken == null) {
            userTokenRepository.save(
                UserToken(
                    user = user,
                    refreshToken = refreshToken,
                    appClient = appClient
                )
            )
            return
        }

        userTokenRepository.save(userToken.copy(refreshToken = refreshToken))
     }

    fun validateRefreshToken(user: User, appClient: AppClient, refreshToken: String): Boolean {
        val userToken: UserToken = userTokenRepository.findByUserAndAppClient(user, appClient) ?: return false

        return userToken.refreshToken == refreshToken
    }
}
