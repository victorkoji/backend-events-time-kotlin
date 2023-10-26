package io.eventstime.repository

import io.eventstime.model.User
import io.eventstime.model.UserToken
import io.eventstime.model.enum.AppClientEnum
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserTokenRepository : JpaRepository<UserToken, Long> {
    fun findByUserAndAppClient(user: User, appClient: AppClientEnum): UserToken?

    fun findByUserIdAndAppClient(userId: Long, appClient: AppClientEnum): UserToken?
}
