package io.eventstime.controller

import io.eventstime.auth.AuthorizationService
import io.eventstime.exception.AuthErrorType
import io.eventstime.exception.CustomException
import io.eventstime.exception.UserErrorType
import io.eventstime.model.*
import io.eventstime.schema.LoginRequest
import io.eventstime.schema.RefreshTokenRequest
import io.eventstime.schema.UserRequest
import io.eventstime.service.TokenService
import io.eventstime.service.UserService
import io.eventstime.service.UserTokenService
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
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
    private val userTokenService = mockk<UserTokenService>()
    private val authService = mockk<AuthorizationService>()
    private val userService = mockk<UserService>()

    private val appClientName = AppClient.CLIENT.name

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

    private val userToken = UserToken(
        id = 1,
        refreshToken = "refresh_token",
        tokenFcm = null,
        appClient = AppClient.CLIENT,
        user = user
    )

    private val userAuth = UserAuth(
        id = 1,
        firstName = "test",
        lastName = "test",
        email = "test@test.com",
        userGroupId = 1
    )

    @Nested
    inner class LoginRouteTest {
        @Test
        fun `Login with success`() {
            // GIVEN
            val payload = LoginRequest(
                email = user.email,
                password = user.password,
                appClient = appClientName
            )

            every {
                userService.findByEmail(payload.email)
            } returns user

            every {
                authService.checkIsValidPassword(payload.password, user.password)
            } returns true

            every {
                tokenService.createAccessToken(user, AppClient.CLIENT)
            } returns "access_token"

            every {
                tokenService.createRefreshToken(user, AppClient.CLIENT)
            } returns "refresh_token"

            every {
                userTokenService.updateRefreshToken(user, AppClient.CLIENT, "refresh_token")
            } just runs

            // WHEN
            val result = assertDoesNotThrow {
                testObject.login(payload)
            }

            // THEN
            assertEquals("access_token", result.accessToken)
            assertEquals("refresh_token", result.refreshToken)

            verify(exactly = 1) { userService.findByEmail(payload.email) }
            verify(exactly = 1) { authService.checkIsValidPassword(payload.password, user.password) }
            verify(exactly = 1) { tokenService.createAccessToken(user, AppClient.CLIENT) }
            verify(exactly = 1) { tokenService.createRefreshToken(user, AppClient.CLIENT) }
            verify(exactly = 1) { userTokenService.updateRefreshToken(user, AppClient.CLIENT, "refresh_token") }
        }

        @Test
        fun `Login throw exception LOGIN_FAILED`() {
            // GIVEN
            val payload = LoginRequest(
                email = user.email,
                password = user.password,
                appClient = appClientName
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
            verify(exactly = 0) { tokenService.createAccessToken(any(), any()) }
            verify(exactly = 0) { tokenService.createRefreshToken(any(), any()) }
            verify(exactly = 0) { userTokenService.updateRefreshToken(user, AppClient.CLIENT, "refresh_token") }
        }

        @Test
        fun `Login throw exception APP_CLIENT_UNDEFINED`() {
            // GIVEN
            val payload = LoginRequest(
                email = user.email,
                password = user.password,
                appClient = "INVALID"
            )

            // WHEN
            val exception = assertThrows<CustomException> {
                testObject.login(payload)
            }

            // THEN
            assertEquals(AuthErrorType.APP_CLIENT_UNDEFINED.name, exception.message)

            verify(exactly = 0) { userService.findByEmail(payload.email) }
            verify(exactly = 0) { authService.checkIsValidPassword(payload.password, user.password) }
            verify(exactly = 0) { tokenService.createAccessToken(any(), any()) }
            verify(exactly = 0) { tokenService.createRefreshToken(any(), any()) }
            verify(exactly = 0) { userTokenService.updateRefreshToken(user, AppClient.CLIENT, "refresh_token") }
        }

        @Test
        fun `Login throw exception on update refreshToken`() {
            // GIVEN
            val payload = LoginRequest(
                email = user.email,
                password = user.password,
                appClient = appClientName
            )

            every {
                userService.findByEmail(payload.email)
            } returns user

            every {
                authService.checkIsValidPassword(payload.password, user.password)
            } returns true

            every {
                tokenService.createAccessToken(user, AppClient.CLIENT)
            } returns "access_token"

            every {
                tokenService.createRefreshToken(user, AppClient.CLIENT)
            } returns "refresh_token"

            every {
                userTokenService.updateRefreshToken(user, AppClient.CLIENT, "refresh_token")
            } throws Exception()

            // WHEN
            val exception = assertThrows<CustomException> {
                testObject.login(payload)
            }

            // THEN
            assertEquals(AuthErrorType.UNAUTHORIZED.name, exception.message)

            verify(exactly = 1) { userService.findByEmail(payload.email) }
            verify(exactly = 1) { authService.checkIsValidPassword(payload.password, user.password) }
            verify(exactly = 1) { tokenService.createAccessToken(any(), any()) }
            verify(exactly = 1) { tokenService.createRefreshToken(any(), any()) }
            verify(exactly = 1) { userTokenService.updateRefreshToken(user, AppClient.CLIENT, "refresh_token") }
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
            } returns Pair(user, AppClient.CLIENT)

            every {
                userTokenService.validateRefreshToken(user, AppClient.CLIENT, refreshTokenRequest.refreshToken)
            } returns true

            every {
                tokenService.createAccessToken(user, AppClient.CLIENT)
            } returns "access_token"

            every {
                tokenService.createRefreshToken(user, AppClient.CLIENT)
            } returns "refresh_token"

            every {
                userTokenService.updateRefreshToken(user, AppClient.CLIENT, "refresh_token")
            } just runs

            // WHEN
            assertDoesNotThrow {
                testObject.refreshToken(refreshTokenRequest)
            }

            // THEN
            verify(exactly = 1) { tokenService.parseRefreshToken(refreshTokenRequest.refreshToken) }
            verify(exactly = 1) { userTokenService.validateRefreshToken(user, AppClient.CLIENT, refreshTokenRequest.refreshToken) }
            verify(exactly = 1) { tokenService.createAccessToken(user, AppClient.CLIENT) }
            verify(exactly = 1) { tokenService.createRefreshToken(user, AppClient.CLIENT) }
            verify(exactly = 1) { userTokenService.updateRefreshToken(user, AppClient.CLIENT, "refresh_token") }
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
            verify(exactly = 0) { userTokenService.validateRefreshToken(user, AppClient.CLIENT, refreshTokenRequest.refreshToken) }
            verify(exactly = 0) { tokenService.createAccessToken(any(), any()) }
            verify(exactly = 0) { tokenService.createRefreshToken(any(), any()) }
            verify(exactly = 0) { userTokenService.updateRefreshToken(user, AppClient.CLIENT, refreshTokenRequest.refreshToken) }
        }

        @Test
        fun `Refresh token throw exception on validate refreshToken`() {
            // GIVEN
            every {
                tokenService.parseRefreshToken(refreshTokenRequest.refreshToken)
            } returns Pair(user, AppClient.CLIENT)

            every {
                userTokenService.validateRefreshToken(user, AppClient.CLIENT, refreshTokenRequest.refreshToken)
            } returns false

            // WHEN
            val exception = assertThrows<CustomException> {
                testObject.refreshToken(refreshTokenRequest)
            }

            // THEN
            assertEquals(AuthErrorType.UNAUTHORIZED.name, exception.message)
            verify(exactly = 1) { tokenService.parseRefreshToken(refreshTokenRequest.refreshToken) }
            verify(exactly = 1) { userTokenService.validateRefreshToken(user, AppClient.CLIENT, refreshTokenRequest.refreshToken) }
            verify(exactly = 0) { tokenService.createAccessToken(user, AppClient.CLIENT) }
            verify(exactly = 0) { tokenService.createRefreshToken(user, AppClient.CLIENT) }
            verify(exactly = 0) { userTokenService.updateRefreshToken(user, AppClient.CLIENT, refreshTokenRequest.refreshToken) }
        }

        @Test
        fun `Refresh token throw exception on update refreshToken`() {
            // GIVEN
            every {
                tokenService.parseRefreshToken(refreshTokenRequest.refreshToken)
            } returns Pair(user, AppClient.CLIENT)

            every {
                userTokenService.validateRefreshToken(user, AppClient.CLIENT, refreshTokenRequest.refreshToken)
            } returns true

            every {
                tokenService.createAccessToken(user, AppClient.CLIENT)
            } returns "access_token"

            every {
                tokenService.createRefreshToken(user, AppClient.CLIENT)
            } returns "refresh_token"

            every {
                userTokenService.updateRefreshToken(user, AppClient.CLIENT, "refresh_token")
            } throws Exception()

            // WHEN
            val exception = assertThrows<CustomException> {
                testObject.refreshToken(refreshTokenRequest)
            }

            // THEN
            assertEquals(AuthErrorType.UNAUTHORIZED.name, exception.message)
            verify(exactly = 1) { tokenService.parseRefreshToken(refreshTokenRequest.refreshToken) }
            verify(exactly = 1) { userTokenService.validateRefreshToken(user, AppClient.CLIENT, refreshTokenRequest.refreshToken) }
            verify(exactly = 0) { tokenService.createAccessToken(user, AppClient.CLIENT) }
            verify(exactly = 1) { tokenService.createRefreshToken(user, AppClient.CLIENT) }
            verify(exactly = 1) { userTokenService.updateRefreshToken(user, AppClient.CLIENT, "refresh_token") }
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
