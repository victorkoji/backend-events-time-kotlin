package io.eventstime.service

import io.eventstime.exception.CustomException
import io.eventstime.exception.EventErrorType
import io.eventstime.exception.StandCategoryErrorType
import io.eventstime.exception.StandErrorType
import io.eventstime.model.Stand
import io.eventstime.repository.StandRepository
import io.eventstime.schema.StandRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class StandService(
    private val standRepository: StandRepository,
    private val eventService: EventService,
    private val standCategoryService: StandCategoryService
) {
    fun findAll(): List<Stand?> {
        return standRepository.findAll()
    }

    fun findById(standId: Long): Stand? {
        return standRepository.findByIdOrNull(standId)
    }

    fun createStand(stand: StandRequest): Stand {
        val event = eventService.findById(stand.eventId)
            ?: throw CustomException(EventErrorType.EVENT_NOT_FOUND)

        val standCategory = standCategoryService.findById(stand.standCategoryId)
            ?: throw CustomException(StandCategoryErrorType.STAND_CATEGORY_NOT_FOUND)

        return standRepository.saveAndFlush(
            Stand(
                name = stand.name,
                isCashier = stand.isCashier,
                event = event,
                standCategory = standCategory
            )
        )
    }

    fun updateStand(standId: Long, stand: StandRequest): Stand {
        val event = eventService.findById(stand.eventId)
            ?: throw CustomException(EventErrorType.EVENT_NOT_FOUND)

        val standCategory = standCategoryService.findById(stand.standCategoryId)
            ?: throw CustomException(StandCategoryErrorType.STAND_CATEGORY_NOT_FOUND)

        val updatedStand = findById(standId)?.copy(
            id = standId,
            name = stand.name,
            isCashier = stand.isCashier,
            event = event,
            standCategory = standCategory
        ) ?: throw CustomException(StandErrorType.STAND_NOT_FOUND)

        return standRepository.saveAndFlush(updatedStand)
    }

    fun deleteStand(standId: Long) {
        findById(standId)?.let {
            standRepository.delete(it)
        } ?: throw CustomException(StandErrorType.STAND_NOT_FOUND)
    }
}
