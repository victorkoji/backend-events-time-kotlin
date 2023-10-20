package io.eventstime.repository

import io.eventstime.model.UserEventStand
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserEventStandRepository : JpaRepository<UserEventStand, Long> {
    fun findAllByUserId(userId: Long): List<UserEventStand>

    fun findByUserIdAndEventId(userId: Long, eventId: Long): List<UserEventStand>
}
