package io.eventstime.service

import io.eventstime.exception.CustomException
import io.eventstime.exception.UserErrorType
import io.eventstime.model.*
import io.eventstime.model.enum.AppClientEnum
import io.eventstime.repository.UserTokenRepository
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class UserTokenServiceTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: UserTokenService

    private val userTokenRepository = mockk<UserTokenRepository>()

    private val userTokenMock = mockk<UserToken>()

    private val appClient = AppClientEnum.CLIENT

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
        appClient = AppClientEnum.CLIENT,
        user = user
    )

    @Test
    fun `Update refresh token with success on token exists`() {
        // GIVEN
        every {
            userTokenRepository.findByUserAndAppClient(any(), any())
        } returns userToken

        every {
            userTokenRepository.save(userToken.copy(refreshToken = refreshToken))
        } returns userTokenMock

        // WHEN
        testObject.updateRefreshToken(user, appClient, refreshToken)

        // THEN
        verify(exactly = 1) { userTokenRepository.findByUserAndAppClient(user, appClient) }
        verify(exactly = 1) { userTokenRepository.save(userToken.copy(refreshToken = refreshToken)) }
    }

    @Test
    fun `Update refresh token with success on token not exists`() {
        // GIVEN
        every {
            userTokenRepository.findByUserAndAppClient(any(), any())
        } returns null

        every {
            userTokenRepository.save(
                UserToken(
                    user = user,
                    refreshToken = refreshToken,
                    appClient = appClient
                )
            )
        } returns userTokenMock

        // WHEN
        testObject.updateRefreshToken(user, appClient, refreshToken)

        // THEN
        verify(exactly = 1) { userTokenRepository.findByUserAndAppClient(user, appClient) }
        verify(exactly = 1) {
            userTokenRepository.save(
                UserToken(
                    user = user,
                    refreshToken = refreshToken,
                    appClient = appClient
                )
            )
        }
    }

    @Test
    fun `Update refresh throw exception with exists userToken`() {
        // GIVEN
        every {
            userTokenRepository.findByUserAndAppClient(any(), any())
        } returns userToken

        every {
            userTokenRepository.save(userToken.copy(refreshToken = refreshToken))
        } throws Exception()

        // WHEN
        val exception = assertThrows<Exception> {
            testObject.updateRefreshToken(user, appClient, refreshToken)
        }

        // THEN
        assertEquals(Exception().message, exception.message)
        verify(exactly = 1) { userTokenRepository.findByUserAndAppClient(user, appClient) }
        verify(exactly = 1) { userTokenRepository.save(userToken.copy(refreshToken = refreshToken)) }
    }

    @Test
    fun `Update refresh throw exception with not exists userToken`() {
        // GIVEN
        every {
            userTokenRepository.findByUserAndAppClient(any(), any())
        } returns null

        every {
            userTokenRepository.save(
                UserToken(
                    user = user,
                    refreshToken = refreshToken,
                    appClient = appClient
                )
            )
        } throws Exception()

        // WHEN
        val exception = assertThrows<Exception> {
            testObject.updateRefreshToken(user, appClient, refreshToken)
        }

        // THEN
        assertEquals(Exception().message, exception.message)
        verify(exactly = 1) { userTokenRepository.findByUserAndAppClient(user, appClient) }
        verify(exactly = 1) {
            userTokenRepository.save(
                UserToken(
                    user = user,
                    refreshToken = refreshToken,
                    appClient = appClient
                )
            )
        }
    }

    @Test
    fun `Insert token fcm with success`() {
        // GIVEN
        val tokenFcm = "12345"
        every {
            userTokenRepository.findByUserIdAndAppClient(userToken.id!!, userToken.appClient)
        } returns userToken

        val updatedUser = userToken.copy(tokenFcm = tokenFcm)

        every {
            userTokenRepository.saveAndFlush(updatedUser)
        } returns updatedUser

        // WHEN
        testObject.insertTokenFcm(userToken.id!!, userToken.appClient, tokenFcm)

        // THEN
        verify(exactly = 1) { userTokenRepository.saveAndFlush(updatedUser) }
    }

    @Test
    fun `Insert token fcm with error user not found`() {
        // GIVEN
        val tokenFcm = "12345"
        every {
            userTokenRepository.findByUserIdAndAppClient(userToken.id!!, userToken.appClient)
        } returns null

        // WHEN
        val result = assertThrows<CustomException> { testObject.insertTokenFcm(user.id!!, userToken.appClient, tokenFcm) }

        // THEN
        assertEquals(UserErrorType.USER_TOKEN_NOT_FOUND.name, result.message)
        verify(exactly = 0) { userTokenRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Delete token fcm with success`() {
        // GIVEN
        every {
            userTokenRepository.findByUserIdAndAppClient(userToken.id!!, userToken.appClient)
        } returns userToken

        val updatedUser = userToken.copy(tokenFcm = null)

        every {
            userTokenRepository.saveAndFlush(updatedUser)
        } returns updatedUser

        // WHEN
        testObject.deleteTokenFcm(userToken.id!!, userToken.appClient)

        // THEN
        verify(exactly = 1) { userTokenRepository.saveAndFlush(updatedUser) }
    }

    @Test
    fun `Delete token fcm with error user not found`() {
        // GIVEN
        every {
            userTokenRepository.findByUserIdAndAppClient(userToken.id!!, userToken.appClient)
        } returns null

        // WHEN
        val result = assertThrows<CustomException> { testObject.deleteTokenFcm(userToken.id!!, userToken.appClient) }

        // THEN
        assertEquals(UserErrorType.USER_TOKEN_NOT_FOUND.name, result.message)
        verify(exactly = 0) { userTokenRepository.saveAndFlush(any()) }
    }
}
