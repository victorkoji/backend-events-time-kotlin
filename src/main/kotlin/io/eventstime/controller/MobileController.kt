package io.eventstime.controller

import io.eventstime.auth.AuthorizationService
import io.eventstime.exception.CustomException
import io.eventstime.exception.EventErrorType
import io.eventstime.mapper.toEventResponse
import io.eventstime.mapper.toResponse
import io.eventstime.schema.EventResponse
import io.eventstime.schema.MenuResponse
import io.eventstime.service.ProductCategoryService
import io.eventstime.service.UserEventStandService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/mobile")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Mobile")
class MobileController(
    private val productCategoryService: ProductCategoryService,
    private val authorizationService: AuthorizationService,
    private val userEventStandService: UserEventStandService
) {

    @GetMapping("/events/{eventId}/menu")
    fun findProductMenu(@PathVariable eventId: Long): List<MenuResponse> {
        return productCategoryService.findMenuByEventId(eventId).toResponse()
    }

    @GetMapping("/events")
    fun findAllEventsByUser(): List<EventResponse> {
        val user = authorizationService.getUser()
        return userEventStandService.findAllEventsByUserId(user.id).toEventResponse()
    }

    @GetMapping("/events/{eventId}")
    fun findEventByUser(@PathVariable eventId: Long): EventResponse? {
        val user = authorizationService.getUser()
        return userEventStandService.findEventByUserId(user.id, eventId)?.toEventResponse()
    }

    @ExceptionHandler
    fun handleException(e: Exception): ResponseStatusException = when (e) {
        is CustomException ->
            ResponseStatusException(
                when (e.message) {
                    EventErrorType.EVENT_NOT_FOUND.name -> HttpStatus.NOT_FOUND
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
        private val log = LoggerFactory.getLogger(MobileController::class.java)!!
    }
}
