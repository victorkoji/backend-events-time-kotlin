package io.eventstime.service

import io.eventstime.exception.CustomException
import io.eventstime.exception.UserErrorType
import io.eventstime.model.UserAuth
import io.eventstime.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class TokenService(
    private val userService: UserService,

    @Autowired
    @Qualifier("jwtDecoderAccessToken")
    private val jwtDecoderAccessToken: JwtDecoder,

    @Autowired
    @Qualifier("jwtEncoderAccessToken")
    private val jwtEncoderAccessToken: JwtEncoder,

    @Autowired
    @Qualifier("jwtDecoderRefreshToken")
    private val jwtDecoderRefreshToken: JwtDecoder,

    @Autowired
    @Qualifier("jwtEncoderRefreshToken")
    private val jwtEncoderRefreshToken: JwtEncoder,

    @Value("\${jwt.access-token.expiration-time-seconds}")
    private val accessTokenExpireSeconds: Long,

    @Value("\${jwt.refresh-token.expiration-time-days}")
    private val refreshTokenExpireDays: Long
) {
    fun createAccessToken(user: User): String {
        val jwsHeader = JwsHeader.with { "HS256" }.build()
        val claims = JwtClaimsSet.builder()
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plus(accessTokenExpireSeconds, ChronoUnit.SECONDS))
            .subject(user.email)
            .claim("userId", user.id)
            .claim("groupId", user.userGroup)
            .build()
        return jwtEncoderAccessToken.encode(JwtEncoderParameters.from(jwsHeader, claims)).tokenValue
    }

    fun createRefreshToken(user: User): String {
        val jwsHeader = JwsHeader.with { "HS256" }.build()
        val claims = JwtClaimsSet.builder()
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plus(refreshTokenExpireDays, ChronoUnit.DAYS))
            .subject(user.email)
            .claim("userId", user.id)
            .build()
        return jwtEncoderRefreshToken.encode(JwtEncoderParameters.from(jwsHeader, claims)).tokenValue
    }

    fun parseAccessToken(token: String): UserAuth? {
        return try {
            val jwt = jwtDecoderAccessToken.decode(token)
            val userId = jwt.claims["userId"] as Long
            val user = userService.findById(userId) ?: throw CustomException(UserErrorType.USER_NOT_FOUND)

            UserAuth(
                id = user.id!!,
                firstName = user.firstName,
                lastName = user.lastName,
                email = user.email,
                tokenFcm = user.tokenFcm,
                userGroupId = user.userGroup!!.id
            )
        } catch (e: Exception) {
            null
        }
    }

    fun parseRefreshToken(token: String): User? {
        return try {
            val jwt = jwtDecoderRefreshToken.decode(token)
            val userId = jwt.claims["userId"] as Long

            return userService.findById(userId) ?: throw CustomException(UserErrorType.USER_NOT_FOUND)
        } catch (e: Exception) {
            null
        }
    }
}
