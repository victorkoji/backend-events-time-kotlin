package io.eventstime.controller

import io.eventstime.exception.CustomException
import io.eventstime.exception.EventErrorType
import io.eventstime.exception.StandCategoryErrorType
import io.eventstime.mapper.toResponse
import io.eventstime.schema.StandCategoryRequest
import io.eventstime.schema.StandCategoryResponse
import io.eventstime.service.StandCategoryService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/stand-categories")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Stand Categories")
class StandCategoryController(
    private val standCategoryService: StandCategoryService
) {
    @GetMapping("/")
    fun findAllStandCategories(): List<StandCategoryResponse?> {
        return standCategoryService.findAll().toResponse()
    }

    @PostMapping("/")
    fun createStandCategory(@RequestBody standCategoryRequest: StandCategoryRequest): StandCategoryResponse {
        return standCategoryService.createStandCategory(standCategoryRequest).toResponse()
    }

    @GetMapping("/{standCategoryId}")
    fun findStandCategory(@PathVariable standCategoryId: Long): StandCategoryResponse? {
        val standCategoryRequest = standCategoryService.findById(standCategoryId) ?: throw CustomException(StandCategoryErrorType.STAND_CATEGORY_NOT_FOUND)
        return standCategoryRequest.toResponse()
    }

    @PutMapping("/{standCategoryId}")
    fun updateStandCategory(
        @PathVariable standCategoryId: Long,
        @RequestBody standCategoryRequest: StandCategoryRequest
    ): StandCategoryResponse {
        return standCategoryService.updateStandCategory(standCategoryId, standCategoryRequest).toResponse()
    }

    @DeleteMapping("/{standCategoryId}")
    fun deleteStandCategory(@PathVariable standCategoryId: Long) {
        standCategoryService.deleteStandCategory(standCategoryId)
    }

    @ExceptionHandler
    fun handleException(e: Exception): ResponseStatusException = when (e) {
        is CustomException ->
            ResponseStatusException(
                when (e.message) {
                    StandCategoryErrorType.STAND_CATEGORY_NOT_FOUND.name, EventErrorType.EVENT_NOT_FOUND.name -> HttpStatus.NOT_FOUND
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
        private val log = LoggerFactory.getLogger(StandCategoryController::class.java)!!
    }
}
