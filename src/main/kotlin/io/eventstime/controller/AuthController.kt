package io.eventstime.controller

import io.eventstime.auth.AuthorizationService
import io.eventstime.exception.AuthErrorType
import io.eventstime.exception.CustomException
import io.eventstime.exception.UserErrorType
import io.eventstime.mapper.toResponse
import io.eventstime.model.AppClient
import io.eventstime.schema.*
import io.eventstime.service.TokenService
import io.eventstime.service.UserService
import io.eventstime.service.UserTokenService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/auth")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Auth")
class AuthController(
    private val tokenService: TokenService,
    private val userTokenService: UserTokenService,
    private val authService: AuthorizationService,
    private val userService: UserService
) {
    @PostMapping("/login")
    fun login(@RequestBody payload: LoginRequest): AuthResponse {
        AppClient.values().any { appClient ->
            appClient.name == payload.appClient
        }.let { exists ->
            if (!exists) throw CustomException(AuthErrorType.LOGIN_FAILED)
        }

        val user = userService.findByEmail(payload.email) ?: throw CustomException(UserErrorType.USER_NOT_FOUND)

        if (!authService.checkIsValidPassword(payload.password, user.password)) {
            throw CustomException(AuthErrorType.LOGIN_FAILED)
        }

        val appClient: AppClient = AppClient.valueOf(payload.appClient)
        val accessToken = tokenService.createAccessToken(user, appClient)
        val refreshToken = tokenService.createRefreshToken(user, appClient)

        userTokenService.updateRefreshToken(user, appClient, refreshToken)

        return AuthResponse(
            accessToken,
            refreshToken
        )
    }

    @GetMapping("/logged")
    fun logged(): UserResponse {
        val userAuth = authService.getUser()
        val user = userService.findById(userAuth.id) ?: throw CustomException(UserErrorType.USER_NOT_FOUND)
        return user.toResponse()
    }

    @PostMapping("/register")
    fun register(@RequestBody payload: UserRequest) {
        if (userService.existUserByEmail(payload.email)) {
            throw CustomException(UserErrorType.EMAIL_ALREADY_EXIST)
        }

        userService.createUser(payload)
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody payload: RefreshTokenRequest): AuthResponse {
        val (user, appClient) = tokenService.parseRefreshToken(payload.refreshToken) ?: throw CustomException(AuthErrorType.UNAUTHORIZED)

        if (!userTokenService.validateRefreshToken(user!!, appClient, payload.refreshToken)) throw CustomException(AuthErrorType.UNAUTHORIZED)

        val refreshToken = tokenService.createRefreshToken(user, appClient)
        userTokenService.updateRefreshToken(user, appClient, refreshToken)

        return AuthResponse(
            tokenService.createAccessToken(user, appClient),
            refreshToken
        )
    }

    @ExceptionHandler
    fun handleException(e: Exception): ResponseStatusException = when (e) {
        is CustomException ->
            ResponseStatusException(
                when (e.message) {
                    AuthErrorType.LOGIN_FAILED.name -> HttpStatus.BAD_REQUEST
                    UserErrorType.EMAIL_ALREADY_EXIST.name -> HttpStatus.CONFLICT
                    UserErrorType.USER_NOT_FOUND.name, AuthErrorType.UNAUTHORIZED.name -> HttpStatus.UNAUTHORIZED
                    else -> HttpStatus.FORBIDDEN
                },
                e.message.toString()
            )
        else -> {
            log.warn(e.message.toString())
            ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.name
            )
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(AuthController::class.java)!!
    }
}
