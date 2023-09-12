package io.eventstime.service

import io.eventstime.exception.CustomException
import io.eventstime.exception.EventErrorType
import io.eventstime.exception.StandCategoryErrorType
import io.eventstime.model.StandCategory
import io.eventstime.repository.StandCategoryRepository
import io.eventstime.schema.StandCategoryRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class StandCategoryService(
    private val standCategoryRepository: StandCategoryRepository,
    private val eventService: EventService
) {
    fun findAll(): List<StandCategory?> {
        return standCategoryRepository.findAll()
    }

    fun findById(standCategoryId: Long): StandCategory? {
        return standCategoryRepository.findByIdOrNull(standCategoryId)
    }

    fun createStandCategory(standCategory: StandCategoryRequest): StandCategory {
        val event = eventService.findById(standCategory.eventId) ?: throw CustomException(EventErrorType.EVENT_NOT_FOUND)

        return standCategoryRepository.saveAndFlush(
            StandCategory(
                name = standCategory.name,
                event = event
            )
        )
    }

    fun updateStandCategory(standCategoryId: Long, standCategory: StandCategoryRequest): StandCategory {
        val event = eventService.findById(standCategory.eventId) ?: throw CustomException(EventErrorType.EVENT_NOT_FOUND)

        val updatedStandCategory = findById(standCategoryId)?.copy(
            id = standCategoryId,
            name = standCategory.name,
            event = event
        ) ?: throw CustomException(StandCategoryErrorType.STAND_CATEGORY_NOT_FOUND)

        return standCategoryRepository.saveAndFlush(updatedStandCategory)
    }

    fun deleteStandCategory(standCategoryId: Long) {
        findById(standCategoryId)?.let { standCategory ->
            standCategoryRepository.delete(standCategory)
        } ?: throw CustomException(StandCategoryErrorType.STAND_CATEGORY_NOT_FOUND)
    }
}
