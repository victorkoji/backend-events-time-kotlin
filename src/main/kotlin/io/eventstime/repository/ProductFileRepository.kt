package io.eventstime.repository

import io.eventstime.model.ProductFile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductFileRepository : JpaRepository<ProductFile, Long>
