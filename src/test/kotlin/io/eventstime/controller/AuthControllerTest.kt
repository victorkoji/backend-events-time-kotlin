package io.eventstime.controller

import io.eventstime.auth.AuthorizationService
import io.eventstime.exception.AuthErrorType
import io.eventstime.exception.CustomException
import io.eventstime.exception.UserErrorType
import io.eventstime.model.User
import io.eventstime.model.UserAuth
import io.eventstime.model.UserGroup
import io.eventstime.schema.LoginRequest
import io.eventstime.schema.RefreshTokenRequest
import io.eventstime.schema.UserRequest
import io.eventstime.service.TokenService
import io.eventstime.service.UserService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class AuthControllerTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: AuthController

    private val tokenService = mockk<TokenService>()
    private val authService = mockk<AuthorizationService>()
    private val userService = mockk<UserService>()

    private val user = User(
        id = 1,
        firstName = "test",
        lastName = "test",
        birthDate = LocalDateTime.now(),
        email = "test@test.com",
        cellphone = "",
        password = "1234",
        tokenFcm = "",
        userGroup = UserGroup(id = 1, name = "admin")
    )

    private val userAuth = UserAuth(
        id = 1,
        firstName = "test",
        lastName = "test",
        email = "test@test.com",
        tokenFcm = "",
        userGroupId = 1
    )

    @Nested
    inner class LoginRouteTest {
        @Test
        fun `Login with success`() {
            // GIVEN
            val payload = LoginRequest(
                email = user.email,
                password = user.password
            )

            every {
                userService.findByEmail(payload.email)
            } returns user

            every {
                authService.checkIsValidPassword(payload.password, user.password)
            } returns true

            every {
                tokenService.createAccessToken(user)
            } returns "access_token"

            every {
                tokenService.createRefreshToken(user)
            } returns "refresh_token"

            // WHEN
            val result = assertDoesNotThrow {
                testObject.login(payload)
            }

            // THEN
            assertEquals("access_token", result.accessToken)
            assertEquals("refresh_token", result.refreshToken)

            verify(exactly = 1) { userService.findByEmail(payload.email) }
            verify(exactly = 1) { authService.checkIsValidPassword(payload.password, user.password) }
            verify(exactly = 1) { tokenService.createAccessToken(user) }
            verify(exactly = 1) { tokenService.createRefreshToken(user) }
        }

        @Test
        fun `Login throw exception LOGIN_FAILED`() {
            // GIVEN
            val payload = LoginRequest(
                email = user.email,
                password = user.password
            )

            every {
                userService.findByEmail(payload.email)
            } returns user

            every {
                authService.checkIsValidPassword(payload.password, user.password)
            } returns false

            // WHEN
            val exception = assertThrows<CustomException> {
                testObject.login(payload)
            }

            // THEN
            assertEquals(AuthErrorType.LOGIN_FAILED.name, exception.message)

            verify(exactly = 1) { userService.findByEmail(payload.email) }
            verify(exactly = 1) { authService.checkIsValidPassword(payload.password, user.password) }
            verify(exactly = 0) { tokenService.createAccessToken(any()) }
            verify(exactly = 0) { tokenService.createRefreshToken(any()) }
        }
    }

    @Nested
    inner class LoggedRouteTest {
        @Test
        fun `Logged with success`() {
            // GIVEN
            every {
                authService.getUser()
            } returns userAuth

            every {
                userService.findById(userAuth.id)
            } returns user

            // WHEN
            val result = assertDoesNotThrow {
                testObject.logged()
            }

            // THEN
            assertEquals(userAuth.id, result.id)
            assertEquals(userAuth.firstName, result.firstName)
            assertEquals(userAuth.lastName, result.lastName)
            assertEquals(userAuth.email, result.email)
            assertEquals(userAuth.tokenFcm, result.tokenFcm)
            assertEquals(userAuth.userGroupId, result.userGroupId)

            verify(exactly = 1) { authService.getUser() }
            verify(exactly = 1) { userService.findById(userAuth.id) }
        }

        @Test
        fun `Logged throw exception`() {
            // GIVEN
            every {
                authService.getUser()
            } throws Exception("error")

            // WHEN
            val exception = assertThrows<Exception> {
                testObject.logged()
            }

            // THEN
            assertEquals("error", exception.message)
            verify(exactly = 1) { authService.getUser() }
            verify(exactly = 0) { userService.findById(userAuth.id) }
        }
    }

    @Nested
    inner class RegisterRouteTest {
        private val userRequest = UserRequest(
            firstName = "test",
            lastName = "test",
            birthDate = LocalDateTime.now(),
            email = "test@test.com",
            cellphone = "",
            password = "1234",
            tokenFcm = "",
            userGroupId = 1
        )

        @Test
        fun `Register with success`() {
            // GIVEN
            every {
                userService.existUserByEmail(userRequest.email)
            } returns false

            every {
                userService.createUser(userRequest)
            } returns mockk()

            // WHEN
            assertDoesNotThrow {
                testObject.register(userRequest)
            }

            // THEN
            verify(exactly = 1) { userService.existUserByEmail(userRequest.email) }
            verify(exactly = 1) { userService.createUser(userRequest) }
        }

        @Test
        fun `Register throw exception`() {
            // GIVEN
            every {
                userService.existUserByEmail(userRequest.email)
            } returns true

            // WHEN
            val exception = assertThrows<CustomException> {
                testObject.register(userRequest)
            }

            // THEN
            assertEquals(UserErrorType.EMAIL_ALREADY_EXIST.name, exception.message)

            verify(exactly = 1) { userService.existUserByEmail(userRequest.email) }
            verify(exactly = 0) { userService.createUser(any()) }
        }
    }

    @Nested
    inner class RefreshTokenRouteTest {
        private val refreshTokenRequest = RefreshTokenRequest(
            refreshToken = "1234"
        )

        @Test
        fun `Refresh token with success`() {
            // GIVEN
            every {
                tokenService.parseRefreshToken(refreshTokenRequest.refreshToken)
            } returns user

            every {
                tokenService.createAccessToken(user)
            } returns "access_token"

            every {
                tokenService.createRefreshToken(user)
            } returns "refresh_token"

            // WHEN
            assertDoesNotThrow {
                testObject.refreshToken(refreshTokenRequest)
            }

            // THEN
            verify(exactly = 1) { tokenService.parseRefreshToken(refreshTokenRequest.refreshToken) }
            verify(exactly = 1) { tokenService.createAccessToken(user) }
            verify(exactly = 1) { tokenService.createRefreshToken(user) }
        }

        @Test
        fun `Refresh token throw exception`() {
            // GIVEN
            every {
                tokenService.parseRefreshToken(refreshTokenRequest.refreshToken)
            } returns null

            // WHEN
            val exception = assertThrows<CustomException> {
                testObject.refreshToken(refreshTokenRequest)
            }

            // THEN
            assertEquals(AuthErrorType.UNAUTHORIZED.name, exception.message)
            verify(exactly = 1) { tokenService.parseRefreshToken(refreshTokenRequest.refreshToken) }
            verify(exactly = 0) { tokenService.createAccessToken(any()) }
            verify(exactly = 0) { tokenService.createRefreshToken(any()) }
        }
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
        fun `Handle error UNAUTHORIZED`() {
            // GIVEN
            val exceptionNotFound = CustomException(UserErrorType.USER_NOT_FOUND)
            val exceptionUnauthorized = CustomException(UserErrorType.USER_NOT_FOUND)

            // WHEN
            val resultNotFound = testObject.handleException(exceptionNotFound)
            val resultUnauthorized = testObject.handleException(exceptionUnauthorized)

            // THEN
            assertEquals(HttpStatus.UNAUTHORIZED, resultNotFound.statusCode)
            assertEquals(HttpStatus.UNAUTHORIZED, resultUnauthorized.statusCode)
        }

        @Test
        fun `Handle error FORBIDDEN`() {
            // GIVEN
            val exception = CustomException()

            // WHEN
            val result = testObject.handleException(exception)

            // THEN
            assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
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
