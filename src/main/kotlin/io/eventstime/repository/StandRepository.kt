package io.eventstime.repository

import io.eventstime.model.Stand
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StandRepository : JpaRepository<Stand, Long>
