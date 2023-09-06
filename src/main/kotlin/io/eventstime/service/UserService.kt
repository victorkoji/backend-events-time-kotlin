package io.eventstime.service

import io.eventstime.exception.CustomException
import io.eventstime.exception.UserErrorType
import io.eventstime.exception.UserGroupErrorType
import io.eventstime.model.User
import io.eventstime.schema.UserRequest
import io.eventstime.repository.UserRepository
import io.eventstime.utils.HashUtils
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class UserService(
    private val userGroupService: UserGroupService,
    private val userRepository: UserRepository,
    private val hashUtils: HashUtils
) {
    fun findAll(): List<User?> {
        return userRepository.findAll()
    }

    fun findById(userId: Long): User? {
        return userRepository.findByIdOrNull(userId)
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    fun existUserByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    fun createUser(user: UserRequest): User {
        val userGroup = userGroupService.findById(user.userGroupId)
            ?: throw CustomException(UserGroupErrorType.GROUP_NOT_FOUND)

        return userRepository.saveAndFlush(
            User(
                firstName = user.firstName,
                lastName = user.firstName,
                birthDate = user.birthDate,
                email = user.email,
                cellphone = user.cellphone,
                password = hashUtils.createHashBcrypt(user.password),
                tokenFcm = "",
                userGroup = userGroup,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )
    }

    fun updateUser(userId: Long, newUser: UserRequest): User {
        val userGroup = userGroupService.findById(newUser.userGroupId)
            ?: throw CustomException(UserGroupErrorType.GROUP_NOT_FOUND)

        val updatedUser = findById(userId)?.copy(
            id = userId,
            firstName = newUser.firstName,
            lastName = newUser.firstName,
            birthDate = newUser.birthDate,
            email = newUser.email,
            cellphone = newUser.cellphone,
            updatedAt = LocalDateTime.now(),
            userGroup = userGroup
        ) ?: throw CustomException(UserErrorType.USER_NOT_FOUND)

        return userRepository.saveAndFlush(updatedUser)
    }

    fun deleteUser(userId: Long) {
        findById(userId)?.let { user ->
            userRepository.delete(user)
        } ?: throw CustomException(UserErrorType.USER_NOT_FOUND)
    }
}
