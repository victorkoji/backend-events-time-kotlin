package io.eventstime.controller

import io.eventstime.exception.CustomException
import io.eventstime.exception.EventErrorType
import io.eventstime.mapper.toResponse
import io.eventstime.schema.EventRequest
import io.eventstime.schema.EventResponse
import io.eventstime.service.EventService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/events")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Events")
class EventController(
    private val eventService: EventService
) {
    @GetMapping("/")
    fun findAllEvents(): List<EventResponse?> {
        return eventService.findAll().toResponse()
    }

    @PostMapping("/")
    fun createEvent(@RequestBody event: EventRequest): EventResponse {
        return eventService.createEvent(event).toResponse()
    }

    @GetMapping("/{eventId}")
    fun findEvent(@PathVariable eventId: Long): EventResponse? {
        val event = eventService.findById(eventId) ?: throw CustomException(EventErrorType.EVENT_NOT_FOUND)
        return event.toResponse()
    }

    @PutMapping("/{eventId}")
    fun updateEvent(@PathVariable eventId: Long, @RequestBody event: EventRequest): EventResponse {
        return eventService.updateEvent(eventId, event).toResponse()
    }

    @DeleteMapping("/{eventId}")
    fun deleteEvent(@PathVariable eventId: Long) {
        eventService.deleteEvent(eventId)
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
        private val log = LoggerFactory.getLogger(EventController::class.java)!!
    }
}
