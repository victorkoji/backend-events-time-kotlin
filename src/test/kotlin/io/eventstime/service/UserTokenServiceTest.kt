package io.eventstime.service

import io.eventstime.exception.AuthErrorType
import io.eventstime.exception.CustomException
import io.eventstime.model.*
import io.eventstime.repository.UserTokenRepository
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.oauth2.jwt.*
import org.springframework.test.context.ActiveProfiles
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class UserTokenServiceTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: UserTokenService

    private val userTokenRepository = mockk<UserTokenRepository>()

    private val userTokenMock = mockk<UserToken>()

    private val appClient = AppClient.CLIENT

    private val refreshToken = "refresh_token"

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

    @Test
    fun `Update refresh token with success`() {
        // GIVEN
        every {
            userTokenRepository.findByUserAndAppClient(any(), any())
        } returns userToken

        every {
            userTokenRepository.save(any())
        } returns userTokenMock

        // WHEN
        testObject.updateRefreshToken(user, appClient, refreshToken)

        // THEN
        verify(exactly = 1) { userTokenRepository.findByUserAndAppClient(user, appClient) }
        verify(exactly = 1) { userTokenRepository.save(any()) }
    }

    @Test
    fun `Update refresh throw exception with exists userToken`() {
        // GIVEN
        every {
            userTokenRepository.findByUserAndAppClient(any(), any())
        } returns userToken

        every {
            userTokenRepository.save(any())
        } throws Exception()

        // WHEN
        val exception = assertThrows<Exception> {
            testObject.updateRefreshToken(user, appClient, refreshToken)
        }

        // THEN
        assertEquals(Exception().message, exception.message)
        verify(exactly = 1) { userTokenRepository.findByUserAndAppClient(user, appClient) }
        verify(exactly = 1) { userTokenRepository.save(any()) }
    }

    @Test
    fun `Update refresh throw exception with not exists userToken`() {
        // GIVEN
        every {
            userTokenRepository.findByUserAndAppClient(any(), any())
        } returns null

        every {
            userTokenRepository.save(any())
        } throws Exception()

        // WHEN
        val exception = assertThrows<Exception> {
            testObject.updateRefreshToken(user, appClient, refreshToken)
        }

        // THEN
        assertEquals(Exception().message, exception.message)
        verify(exactly = 1) { userTokenRepository.findByUserAndAppClient(user, appClient) }
        verify(exactly = 1) { userTokenRepository.save(any()) }
    }
}
