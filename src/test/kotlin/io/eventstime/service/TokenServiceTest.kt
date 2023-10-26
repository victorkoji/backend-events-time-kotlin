package io.eventstime.service

import io.eventstime.model.User
import io.eventstime.model.UserAuth
import io.eventstime.model.UserGroup
import io.eventstime.model.enum.AppClientEnum
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
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
class TokenServiceTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: TokenService

    private val userService = mockk<UserService>()
    private val jwtDecoderAccessToken = mockk<JwtDecoder>()
    private val jwtEncoderAccessToken = mockk<JwtEncoder>()
    private val jwtDecoderRefreshToken = mockk<JwtDecoder>()
    private val jwtEncoderRefreshToken = mockk<JwtEncoder>()

    private val accessTokenExpireSeconds: Long = 10
    private val refreshTokenExpireDays: Long = 1

    private val appClientName = AppClientEnum.CLIENT.name

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
    fun `Create access token with success`() {
        // GIVEN
        val jwsHeader = JwsHeader.with { "HS256" }.build()
        val claims = JwtClaimsSet.builder()
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plus(accessTokenExpireSeconds, ChronoUnit.SECONDS))
            .subject(user.email)
            .claim("userId", user.id)
            .claim("groupId", user.userGroup)
            .claim("appClient", appClientName)
            .build()

        every {
            jwtEncoderAccessToken.encode(any())
        } returns mockk()

        val jwt = jwtEncoderAccessToken.encode(JwtEncoderParameters.from(jwsHeader, claims))

        every {
            jwt.tokenValue
        } returns "teste"

        // WHEN
        val result = testObject.createAccessToken(user, AppClientEnum.CLIENT)

        // THEN
        assertEquals(jwt.tokenValue, result)
    }

    @Test
    fun `Create refresh token with success`() {
        // GIVEN
        val jwsHeader = JwsHeader.with { "HS256" }.build()
        val claims = JwtClaimsSet.builder()
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plus(refreshTokenExpireDays, ChronoUnit.DAYS))
            .subject(user.email)
            .claim("userId", user.id)
            .claim("appClient", appClientName)
            .build()

        every {
            jwtEncoderRefreshToken.encode(any())
        } returns mockk()

        val jwt = jwtEncoderRefreshToken.encode(JwtEncoderParameters.from(jwsHeader, claims))

        every {
            jwt.tokenValue
        } returns "teste"

        // WHEN
        val result = testObject.createRefreshToken(user, AppClientEnum.CLIENT)

        // THEN
        assertEquals(jwt.tokenValue, result)
    }

    @Test
    fun `Parse access token with success`() {
        // GIVEN
        val token = "teste"

        every {
            jwtDecoderAccessToken.decode(token)
        } returns mockk()

        every {
            jwtDecoderAccessToken.decode(token).claims
        } returns mockk()

        every {
            jwtDecoderAccessToken.decode(token).claims["userId"]
        } returns user.id!!

        every {
            jwtDecoderAccessToken.decode(token).claims["appClient"]
        } returns AppClientEnum.CLIENT.name

        every {
            userService.findById(1)
        } returns user

        // WHEN
        val result = testObject.parseAccessToken(token)

        // THEN
        assertEquals(
            UserAuth(id = 1, firstName = "test", lastName = "test", email = "test@test.com", userGroupId = 1, appClient = AppClientEnum.CLIENT),
            result
        )
        verify(exactly = 1) { userService.findById(1) }
    }

    @Test
    fun `Parse access token return null`() {
        // GIVEN
        val token = "teste"

        every {
            jwtDecoderAccessToken.decode(token)
        } throws Exception("error")

        // WHEN
        val result = testObject.parseAccessToken(token)

        // THEN
        assertNull(result)
        verify(exactly = 0) { userService.findById(any()) }
    }

    @Test
    fun `Parse refresh token with success`() {
        // GIVEN
        val token = "teste"

        every {
            jwtDecoderRefreshToken.decode(token)
        } returns mockk()

        every {
            jwtDecoderRefreshToken.decode(token).claims
        } returns mockk()

        every {
            jwtDecoderRefreshToken.decode(token).claims["userId"]
        } returns user.id!!

        every {
            jwtDecoderRefreshToken.decode(token).claims["appClient"]
        } returns appClientName

        every {
            userService.findById(user.id!!)
        } returns user

        // WHEN
        val result = testObject.parseRefreshToken(token)

        // THEN
        assertEquals(Pair(user, AppClientEnum.CLIENT), result)
        verify(exactly = 1) { userService.findById(user.id!!) }
    }

    @Test
    fun `Parse refresh token returns null`() {
        // GIVEN
        val token = "teste"

        every {
            jwtDecoderRefreshToken.decode(token)
        } throws Exception("error")

        // WHEN
        val result = testObject.parseRefreshToken(token)

        // THEN
        assertNull(result)
        verify(exactly = 0) { userService.findById(any()) }
    }
}
