package io.eventstime.service

import io.eventstime.model.User
import io.eventstime.model.UserRequest
import io.eventstime.repository.UserRepository
import io.eventstime.utils.HashUtils
import io.eventstime.utils.modelMapperUtils
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userGroupService: UserGroupService,
    private val userRepository: UserRepository,
    private val modelMapperUtils: modelMapperUtils,
    private val hashUtils: HashUtils
) {
    fun findAll(): List<User> {
        return userRepository.findAll()
    }

    fun findById(userId: Long): User {
        return userRepository.findByIdOrNull(userId) ?: throw Exception("Not found")
    }

    fun findByEmail(email: String): User {
        return userRepository.findByEmail(email)
    }

    fun createUser(user: UserRequest): User {
        val userGroup = userGroupService.findById(user.userGroupId)

        return userRepository.save(
            User(
                firstName = user.firstName,
                lastName = user.firstName,
                birthDate = user.birthDate,
                email = user.email,
                cellphone = user.cellphone,
                password = hashUtils.createHashBcrypt(user.password),
                tokenFcm = "",
                userGroup = userGroup,
                createdAt = Date(),
                updatedAt = Date()
            )
        )
    }

    fun updateUser(userId: Long, newUser: UserRequest): User {
        val userGroup = userGroupService.findById(newUser.userGroupId)
        val user = findById(userId)

        val updatedUser = user.copy(
            id = userId,
            firstName = newUser.firstName,
            lastName = newUser.firstName,
            birthDate = newUser.birthDate,
            email = newUser.email,
            cellphone = newUser.cellphone,
            updatedAt = Date(),
            userGroup = userGroup
        )

        return userRepository.save(updatedUser)
    }

    fun deleteUser(userId: Long) {
        val user = userRepository.findById(userId).orElseThrow { Exception("Not found") }
        val deletedUser = user.copy(deletedAt = Date())
        userRepository.save(deletedUser)
    }
}
