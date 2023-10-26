package io.eventstime.controller

import io.eventstime.auth.AuthorizationService
import io.eventstime.exception.CustomException
import io.eventstime.exception.UserErrorType
import io.eventstime.exception.UserGroupErrorType
import io.eventstime.mapper.toResponse
import io.eventstime.schema.TokenFcmRequest
import io.eventstime.schema.UserRequest
import io.eventstime.schema.UserResponse
import io.eventstime.service.UserService
import io.eventstime.service.UserTokenService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users")
class UserController(
    private val authorizationService: AuthorizationService,
    private val userService: UserService,
    private val userTokenService: UserTokenService
) {
    @GetMapping("/")
    fun findAllUser(): List<UserResponse?> {
        val userList = userService.findAll()
        return userList.toResponse()
    }

    @PostMapping("/")
    fun createUser(@RequestBody user: UserRequest): UserResponse {
        return userService.createUser(user).toResponse()
    }

    @GetMapping("/{userId}")
    fun findUser(@PathVariable userId: Long): UserResponse? {
        val user = userService.findById(userId) ?: throw CustomException(UserErrorType.USER_NOT_FOUND)
        return user.toResponse()
    }

    @PutMapping("/{userId}")
    fun updateUser(@PathVariable userId: Long, @RequestBody user: UserRequest): UserResponse {
        return userService.updateUser(userId, user).toResponse()
    }

    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable userId: Long) {
        userService.deleteUser(userId)
    }

    @PostMapping("/token-fcm")
    fun insertTokenFcm(@RequestBody tokenFcmRequest: TokenFcmRequest) {
        val userAuth = authorizationService.getUser()
        userTokenService.insertTokenFcm(userAuth.id, userAuth.appClient, tokenFcmRequest.tokenFcm)
    }

    @DeleteMapping("/token-fcm")
    fun deleteTokenFcm() {
        val userAuth = authorizationService.getUser()
        userTokenService.deleteTokenFcm(userAuth.id, userAuth.appClient)
    }

    @ExceptionHandler
    fun handleException(e: Exception): ResponseStatusException = when (e) {
        is CustomException ->
            ResponseStatusException(
                when (e.message) {
                    UserErrorType.EMAIL_ALREADY_EXIST.name -> HttpStatus.CONFLICT
                    UserErrorType.USER_NOT_FOUND.name, UserGroupErrorType.GROUP_NOT_FOUND.name -> HttpStatus.NOT_FOUND
                    else -> HttpStatus.BAD_REQUEST
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
        private val log = LoggerFactory.getLogger(UserController::class.java)!!
    }
}
