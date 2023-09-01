package io.eventstime.service

import io.eventstime.model.UserGroup
import io.eventstime.repository.UserGroupRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserGroupService(
    private val userGroupRepository: UserGroupRepository
) {
    fun findById(userGroupId: Long): UserGroup? {
        return userGroupRepository.findByIdOrNull(userGroupId)
    }
}
