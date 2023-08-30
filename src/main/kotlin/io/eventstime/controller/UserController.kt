package io.eventstime.controller

import io.eventstime.mapper.toResponse
import io.eventstime.model.UserRequest
import io.eventstime.model.UserResponse
import io.eventstime.service.UserService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users")
class UserController(
    private val userService: UserService
) {
    @GetMapping("/")
    fun findAllUser(): List<UserResponse> {
        return try {
            userService.findAll().toResponse()
        } catch (e: Exception) {
            log.warn("Error to create user. Reason: ${e.message}")
            throw e
        }
    }

    @PostMapping("/")
    fun createUser(@RequestBody user: UserRequest): UserResponse {
        return try {
            userService.createUser(user).toResponse()
        } catch (e: Exception) {
            log.warn("Error to create user. Reason: ${e.message}")
            throw e
        }
    }

    @GetMapping("/{userId}")
    fun findUser(authentication: Authentication, @PathVariable userId: Long): UserResponse? {
        return try {
            log.info("Received getUser: $userId")
            userService.findById(userId).toResponse()
        } catch (e: Exception) {
            log.warn("Error to find user. userId: $userId. Reason: ${e.message}")
            throw e
        }
    }

    @PutMapping("/{userId}")
    fun updateUser(@PathVariable userId: Long, @RequestBody user: UserRequest): UserResponse {
        return try {
            userService.updateUser(userId, user).toResponse()
        } catch (e: Exception) {
            log.warn("Error to create user. Reason: ${e.message}")
            throw e
        }
    }

    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable userId: Long) {
        try {
            userService.deleteUser(userId)
        } catch (e: Exception) {
            log.warn("Error to delete user. Reason: ${e.message}")
            throw e
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(UserController::class.java)!!
    }
}
