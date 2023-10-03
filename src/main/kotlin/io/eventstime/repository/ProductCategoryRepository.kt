package io.eventstime.repository

import io.eventstime.model.ProductCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductCategoryRepository : JpaRepository<ProductCategory, Long> {
    fun findAllByEventIdOrderByNameAsc(eventId: Long): List<ProductCategory>
}
