package io.eventstime.controller

import io.eventstime.model.dto.ApiException
import io.eventstime.model.dto.LoginDto
import io.eventstime.model.dto.LoginResponseDto
import io.eventstime.utils.HashUtils
import io.eventstime.service.TokenService
import io.eventstime.service.UserService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// @CrossOrigin(origins = ["*"], maxAge = 3600)
// @CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
class AuthController(
    private val hashService: HashUtils,
    private val tokenService: TokenService,
    private val userService: UserService
) {
    @PostMapping("/login")
    fun login(@RequestBody payload: LoginDto): LoginResponseDto {
        val user = userService.findByEmail(payload.email) ?: throw ApiException(400, "Login failed")

        if (!hashService.checkBcrypt(payload.password, user.password)) {
            throw ApiException(400, "Login failed")
        }

        return LoginResponseDto(
            accessToken = tokenService.createAccessToken(user),
            refreshToken = tokenService.createRefreshToken(user)
        )
    }

//    @GetMapping("/logged")
//    fun logged(@RequestBody payload: LoginDto): LoginResponseDto {
//        val user = userService.findByEmail(payload.email) ?: throw ApiException(400, "Login failed")
//
//        if (!hashService.checkBcrypt(payload.password, user.password)) {
//            throw ApiException(400, "Login failed")
//        }
//
//        return LoginResponseDto(
//            token = tokenService.createToken(user)
//        )
//    }

//    @PostMapping("/register")
//    fun register(@RequestBody payload: LoginDto): LoginResponseDto {
//        val user = userService.findByEmail(payload.email) ?: throw ApiException(400, "Login failed")
//
//        if (!hashService.checkBcrypt(payload.password, user.password)) {
//            throw ApiException(400, "Login failed")
//        }
//
//        return LoginResponseDto(
//            token = tokenService.createToken(user)
//        )
//    }

//    @PostMapping("/refresh-token")
//    fun refreshToken(@RequestBody payload: LoginDto): LoginResponseDto {
//        val user = userService.findByEmail(payload.email) ?: throw ApiException(400, "Login failed")
//
//        if (!hashService.checkBcrypt(payload.password, user.password)) {
//            throw ApiException(400, "Login failed")
//        }
//
//        return LoginResponseDto(
//            token = tokenService.createToken(user)
//        )
//    }
}
