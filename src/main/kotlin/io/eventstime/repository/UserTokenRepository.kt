package io.eventstime.repository

import io.eventstime.model.AppClient
import io.eventstime.model.User
import io.eventstime.model.UserToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserTokenRepository : JpaRepository<UserToken, Long> {
    fun findByUserAndAppClient(user: User, appClient: AppClient): UserToken?
}
