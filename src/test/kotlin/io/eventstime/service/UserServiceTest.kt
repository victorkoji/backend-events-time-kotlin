package io.eventstime.service

import io.eventstime.exception.CustomException
import io.eventstime.exception.UserErrorType
import io.eventstime.exception.UserGroupErrorType
import io.eventstime.model.User
import io.eventstime.model.UserGroup
import io.eventstime.repository.UserRepository
import io.eventstime.schema.UserRequest
import io.eventstime.utils.HashUtils
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class UserServiceTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: UserService

    private val userRepository = mockk<UserRepository>()
    private val userGroupService = mockk<UserGroupService>()
    private val hashUtils = mockk<HashUtils>()

    private val user = User(
        id = 1,
        firstName = "firstName",
        lastName = "lastName",
        birthDate = LocalDateTime.now(),
        email = "teste@gmail.com",
        cellphone = "123123",
        password = "password",
        userGroup = UserGroup(id = 1, name = "group-test")
    )

    @Test
    fun `Find all users list not empty`() {
        // GIVEN
        every {
            userRepository.findAll()
        } returns listOf(user)

        // WHEN
        val result = testObject.findAll()

        // THEN
        assertEquals(1, result.size)
        assertEquals(1, user.id)

        verify(exactly = 1) { userRepository.findAll() }
    }

    @Test
    fun `Find all users list empty`() {
        // GIVEN
        every {
            userRepository.findAll()
        } returns emptyList()

        // WHEN
        val result = testObject.findAll()

        // THEN
        assertEquals(0, result.size)
        verify(exactly = 1) { userRepository.findAll() }
    }

    @Test
    fun `Find user by id return object user`() {
        // GIVEN
        every {
            userRepository.findById(user.id!!)
        } returns Optional.of(user)

        // WHEN
        val result = testObject.findById(user.id!!)

        // THEN
        assertNotNull(result)

        verify(exactly = 1) { userRepository.findById(user.id!!) }
    }

    @Test
    fun `Find user by id return null`() {
        // GIVEN
        val userId = 1L

        every {
            userRepository.findById(userId)
        } returns Optional.empty()

        // WHEN
        val result = testObject.findById(userId)

        // THEN
        assertNull(result)
        verify(exactly = 1) { userRepository.findById(userId) }
    }

    @Test
    fun `Find user by email return object user`() {
        // GIVEN
        every {
            userRepository.findByEmail(user.email)
        } returns user

        // WHEN
        val result = testObject.findByEmail(user.email)

        // THEN
        assertNotNull(result)

        verify(exactly = 1) { userRepository.findByEmail(user.email) }
    }

    @Test
    fun `Find user by email return null`() {
        // GIVEN
        every {
            userRepository.findByEmail(user.email)
        } returns null

        // WHEN
        val result = testObject.findByEmail(user.email)

        // THEN
        assertNull(result)
        verify(exactly = 1) { userRepository.findByEmail(user.email) }
    }

    @Test
    fun `Exist user by email return true`() {
        // GIVEN
        every {
            userRepository.existsByEmail(user.email)
        } returns true

        // WHEN
        val result = testObject.existUserByEmail(user.email)

        // THEN
        assertTrue(result)

        verify(exactly = 1) { userRepository.existsByEmail(user.email) }
    }

    @Test
    fun `Exist user by email return false`() {
        // GIVEN
        every {
            userRepository.existsByEmail(user.email)
        } returns false

        // WHEN
        val result = testObject.existUserByEmail(user.email)

        // THEN
        assertFalse(result)
        verify(exactly = 1) { userRepository.existsByEmail(user.email) }
    }

    @Test
    fun `Create user with success`() {
        // GIVEN
        val userRequest = UserRequest(
            firstName = user.firstName,
            lastName = user.lastName,
            birthDate = user.birthDate,
            email = user.email,
            cellphone = user.cellphone,
            password = user.password,
            userGroupId = 1
        )
        val passwordEncrypted = "password-encrypt"

        every {
            userGroupService.findById(userRequest.userGroupId)
        } returns user.userGroup

        every {
            hashUtils.createHashBcrypt(user.password)
        } returns passwordEncrypted

        every {
            userRepository.saveAndFlush(any())
        } returns user

        // WHEN
        val result = testObject.createUser(userRequest)

        // THEN
        assertNotNull(result)
        verify(exactly = 1) { userRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Create user with group not found error`() {
        // GIVEN
        val userRequest = UserRequest(
            firstName = user.firstName,
            lastName = user.lastName,
            birthDate = user.birthDate,
            email = user.email,
            cellphone = user.cellphone,
            password = user.password,
            userGroupId = 1
        )

        every {
            userGroupService.findById(userRequest.userGroupId)
        } returns null

        every {
            hashUtils.createHashBcrypt(user.password)
        } returns "password-encrypt"

        // WHEN
        val result = assertThrows<CustomException> { testObject.createUser(userRequest) }

        // THEN
        assertEquals(UserGroupErrorType.GROUP_NOT_FOUND.name, result.message)
        verify(exactly = 0) { hashUtils.createHashBcrypt(user.password) }
        verify(exactly = 0) { userRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Create user with error to save`() {
        // GIVEN
        val userRequest = UserRequest(
            firstName = user.firstName,
            lastName = user.lastName,
            birthDate = user.birthDate,
            email = user.email,
            cellphone = user.cellphone,
            password = user.password,
            userGroupId = 1
        )

        every {
            userGroupService.findById(userRequest.userGroupId)
        } returns user.userGroup

        every {
            hashUtils.createHashBcrypt(user.password)
        } returns "password-encrypt"

        every {
            userRepository.saveAndFlush(any())
        } throws Exception("error")

        // WHEN
        val result = assertThrows<Exception> { testObject.createUser(userRequest) }

        // THEN
        assertEquals("error", result.message)
        verify(exactly = 1) { userRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Update user with success`() {
        // GIVEN
        val userRequest = UserRequest(
            firstName = user.firstName,
            lastName = user.lastName,
            birthDate = user.birthDate,
            email = user.email,
            cellphone = user.cellphone,
            password = user.password,
            userGroupId = 1
        )

        every {
            userGroupService.findById(userRequest.userGroupId)
        } returns UserGroup(id = 1, name = "group-test")

        every {
            userRepository.findById(user.id!!)
        } returns Optional.of(user)

        every {
            userRepository.saveAndFlush(any())
        } returns user

        // WHEN
        val result = testObject.updateUser(user.id!!, userRequest)

        // THEN
        assertNotNull(result)
        verify(exactly = 1) { userRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Update user with error user group not found`() {
        // GIVEN
        val userRequest = UserRequest(
            firstName = user.firstName,
            lastName = user.lastName,
            birthDate = user.birthDate,
            email = user.email,
            cellphone = user.cellphone,
            password = user.password,
            userGroupId = 1
        )

        every {
            userGroupService.findById(userRequest.userGroupId)
        } returns null

        // WHEN
        val result = assertThrows<CustomException> { testObject.updateUser(user.id!!, userRequest) }

        // THEN
        assertEquals(UserGroupErrorType.GROUP_NOT_FOUND.name, result.message)
        verify(exactly = 0) { userRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Update user with error user not found`() {
        // GIVEN
        val userRequest = UserRequest(
            firstName = user.firstName,
            lastName = user.lastName,
            birthDate = user.birthDate,
            email = user.email,
            cellphone = user.cellphone,
            password = user.password,
            userGroupId = 1
        )

        every {
            userGroupService.findById(userRequest.userGroupId)
        } returns UserGroup(id = 1, name = "group-test")

        every {
            userRepository.findById(user.id!!)
        } returns Optional.empty()

        every {
            userRepository.saveAndFlush(user)
        } returns user

        // WHEN
        val result = assertThrows<CustomException> { testObject.updateUser(user.id!!, userRequest) }

        // THEN
        assertEquals(UserErrorType.USER_NOT_FOUND.name, result.message)
        verify(exactly = 0) { userRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Delete user with success`() {
        // GIVEN
        every {
            userRepository.findById(user.id!!)
        } returns Optional.of(user)

        every {
            userRepository.delete(user)
        } just runs

        // WHEN
        val result = testObject.deleteUser(user.id!!)

        // THEN
        assertNotNull(result)
        verify(exactly = 1) { userRepository.delete(user) }
    }

    @Test
    fun `Delete user with error user not found`() {
        // GIVEN
        every {
            userRepository.findById(user.id!!)
        } returns Optional.empty()

        every {
            userRepository.delete(user)
        } just runs

        // WHEN
        val result = assertThrows<CustomException> { testObject.deleteUser(user.id!!) }

        // THEN
        assertEquals(UserErrorType.USER_NOT_FOUND.name, result.message)
        verify(exactly = 0) { userRepository.delete(any()) }
    }

    @Test
    fun `Insert token fcm with success`() {
        // GIVEN
        val tokenFcm = "12345"
        every {
            userRepository.findById(user.id!!)
        } returns Optional.of(user)

        val updatedUser = user.copy(tokenFcm = tokenFcm)

        every {
            userRepository.saveAndFlush(updatedUser)
        } returns updatedUser

        // WHEN
        testObject.insertTokenFcm(user.id!!, tokenFcm)

        // THEN
        verify(exactly = 1) { userRepository.saveAndFlush(updatedUser) }
    }

    @Test
    fun `Insert token fcm with error user not found`() {
        // GIVEN
        val tokenFcm = "12345"
        every {
            userRepository.findById(user.id!!)
        } returns Optional.empty()

        // WHEN
        val result = assertThrows<CustomException> { testObject.insertTokenFcm(user.id!!, tokenFcm) }

        // THEN
        assertEquals(UserErrorType.USER_NOT_FOUND.name, result.message)
        verify(exactly = 0) { userRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Delete token fcm with success`() {
        // GIVEN
        every {
            userRepository.findById(user.id!!)
        } returns Optional.of(user)

        val updatedUser = user.copy(tokenFcm = null)

        every {
            userRepository.saveAndFlush(updatedUser)
        } returns updatedUser

        // WHEN
        testObject.deleteTokenFcm(user.id!!)

        // THEN
        verify(exactly = 1) { userRepository.saveAndFlush(updatedUser) }
    }

    @Test
    fun `Delete token fcm with error user not found`() {
        // GIVEN
        val tokenFcm = "12345"
        every {
            userRepository.findById(user.id!!)
        } returns Optional.empty()

        // WHEN
        val result = assertThrows<CustomException> { testObject.deleteTokenFcm(user.id!!) }

        // THEN
        assertEquals(UserErrorType.USER_NOT_FOUND.name, result.message)
        verify(exactly = 0) { userRepository.saveAndFlush(any()) }
    }
}
