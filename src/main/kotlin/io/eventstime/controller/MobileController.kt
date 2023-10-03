package io.eventstime.controller

import io.eventstime.exception.CustomException
import io.eventstime.mapper.toResponse
import io.eventstime.schema.MenuResponse
import io.eventstime.service.ProductCategoryService
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
    private val productCategoryService: ProductCategoryService
) {

    @GetMapping("/products/menu")
    fun findProductMenu(@RequestParam eventId: Long): List<MenuResponse> {
        return productCategoryService.findMenuByEventId(eventId).toResponse()
    }

    @ExceptionHandler
    fun handleException(e: Exception): ResponseStatusException = when (e) {
        is CustomException ->
            ResponseStatusException(
                when (e.message) {
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
