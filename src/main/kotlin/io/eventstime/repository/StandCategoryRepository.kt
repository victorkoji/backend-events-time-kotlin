package io.eventstime.repository

import io.eventstime.model.StandCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StandCategoryRepository : JpaRepository<StandCategory, Long>
