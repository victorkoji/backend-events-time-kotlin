package io.eventstime.controller

import io.eventstime.exception.CustomException
import io.eventstime.exception.EventErrorType
import io.eventstime.exception.StandCategoryErrorType
import io.eventstime.exception.StandErrorType
import io.eventstime.mapper.toResponse
import io.eventstime.schema.StandRequest
import io.eventstime.schema.StandResponse
import io.eventstime.service.StandService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/stands")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Stands")
class StandController(
    private val standService: StandService
) {
    @GetMapping("/")
    fun findAllStands(): List<StandResponse?> {
        return standService.findAll().toResponse()
    }

    @PostMapping("/")
    fun createStand(@RequestBody standRequest: StandRequest): StandResponse {
        return standService.createStand(standRequest).toResponse()
    }

    @GetMapping("/{standId}")
    fun findStand(@PathVariable standId: Long): StandResponse? {
        val stand = standService.findById(standId) ?: throw CustomException(StandErrorType.STAND_NOT_FOUND)
        return stand.toResponse()
    }

    @PutMapping("/{standId}")
    fun updateStand(
        @PathVariable standId: Long,
        @RequestBody standRequest: StandRequest
    ): StandResponse {
        return standService.updateStand(standId, standRequest).toResponse()
    }

    @DeleteMapping("/{standId}")
    fun deleteStand(@PathVariable standId: Long) {
        standService.deleteStand(standId)
    }

    @ExceptionHandler
    fun handleException(e: Exception): ResponseStatusException = when (e) {
        is CustomException ->
            ResponseStatusException(
                when (e.message) {
                    StandErrorType.STAND_NOT_FOUND.name,
                    StandCategoryErrorType.STAND_CATEGORY_NOT_FOUND.name,
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
        private val log = LoggerFactory.getLogger(StandController::class.java)
    }
}
