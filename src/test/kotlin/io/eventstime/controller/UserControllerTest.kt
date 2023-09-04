package io.eventstime.controller

import io.eventstime.exception.CustomException
import io.eventstime.exception.UserErrorType
import io.eventstime.exception.UserGroupErrorType
import io.eventstime.model.User
import io.eventstime.model.UserGroup
import io.eventstime.schema.UserRequest
import io.eventstime.service.UserService
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class UserControllerTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: UserController

    private val userService = mockk<UserService>()

    private val user = User(
        id = 1,
        firstName = "test",
        lastName = "test",
        birthDate = LocalDateTime.now(),
        email = "test@test.com",
        cellphone = "",
        password = "1234",
        userGroup = UserGroup(id = 1, name = "admin")
    )

    @Test
    fun `Find all user with list not empty`() {
        // GIVEN
        every {
            userService.findAll()
        } returns listOf(user)

        // WHEN
        val result = testObject.findAllUser()

        // THEN
        assertEquals(1, result.size)
        verify(exactly = 1) { userService.findAll() }
    }

    @Test
    fun `Find all user with list empty`() {
        // GIVEN
        every {
            userService.findAll()
        } returns emptyList()

        // WHEN
        val result = testObject.findAllUser()

        // THEN
        assertEquals(0, result.size)
        verify(exactly = 1) { userService.findAll() }
    }

    @Test
    fun `Create user with success`() {
        // GIVEN
        val userRequest = UserRequest(
            firstName = "test1",
            lastName = "test1",
            birthDate = LocalDateTime.now(),
            email = "test@test.com",
            cellphone = "61213123",
            password = "password",
            userGroupId = 1
        )

        every {
            userService.createUser(userRequest)
        } returns user

        // WHEN
        assertDoesNotThrow {
            testObject.createUser(userRequest)
        }

        // THEN
        verify(exactly = 1) { userService.createUser(userRequest) }
    }

    @Test
    fun `Update user with success`() {
        // GIVEN
        val userRequest = UserRequest(
            firstName = "test1",
            lastName = "test1",
            birthDate = LocalDateTime.now(),
            email = "test@test.com",
            cellphone = "61213123",
            password = "password",
            userGroupId = 1
        )

        every {
            userService.updateUser(user.id!!, userRequest)
        } returns user

        // WHEN
        assertDoesNotThrow {
            testObject.updateUser(user.id!!, userRequest)
        }

        // THEN
        verify(exactly = 1) { userService.updateUser(user.id!!, userRequest) }
    }

    @Test
    fun `Delete user with success`() {
        // GIVEN
        every {
            userService.deleteUser(user.id!!)
        } just runs

        // WHEN
        assertDoesNotThrow {
            testObject.deleteUser(user.id!!)
        }

        // THEN
        verify(exactly = 1) { userService.deleteUser(user.id!!) }
    }

    @Test
    fun `Find user with success`() {
        // GIVEN
        every {
            userService.findById(user.id!!)
        } returns user

        // WHEN
        assertDoesNotThrow {
            testObject.findUser(user.id!!)
        }

        // THEN
        verify(exactly = 1) { userService.findById(user.id!!) }
    }

    @Nested
    inner class HandleErrorTest {

        @Test
        fun `Handle error CONFLICT`() {
            // GIVEN
            val exception = CustomException(UserErrorType.EMAIL_ALREADY_EXIST)

            // WHEN
            val result = testObject.handleException(exception)

            // THEN
            assertEquals(HttpStatus.CONFLICT, result.statusCode)
        }

        @Test
        fun `Handle error NOT_FOUND`() {
            // GIVEN
            val exceptionUserNotFound = CustomException(UserErrorType.USER_NOT_FOUND)
            val exceptionUserGroupNotFound = CustomException(UserGroupErrorType.GROUP_NOT_FOUND)

            // WHEN
            val resultUserNotFound = testObject.handleException(exceptionUserNotFound)
            val resultUserGroupNotFound = testObject.handleException(exceptionUserGroupNotFound)

            // THEN
            assertEquals(HttpStatus.NOT_FOUND, resultUserNotFound.statusCode)
            assertEquals(HttpStatus.NOT_FOUND, resultUserGroupNotFound.statusCode)
        }

        @Test
        fun `Handle error BAD_REQUEST`() {
            // GIVEN
            val exception = CustomException()

            // WHEN
            val result = testObject.handleException(exception)

            // THEN
            assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        }

        @Test
        fun `Handle error generic exception`() {
            // GIVEN
            val exception = Exception("error")

            // WHEN
            val result = testObject.handleException(exception)

            // THEN
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.statusCode)
        }
    }
}
