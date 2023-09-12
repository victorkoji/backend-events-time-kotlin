package io.eventstime.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.annotations.Where
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "events")
@SQLDelete(sql = "UPDATE events SET deleted_at=NOW() WHERE id = ?")
@Where(clause = "deleted_at is NULL")
data class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = true)
    val id: Long? = null,

    val name: String,

    @Column(nullable = true)
    val address: String?,
    val isPublic: Boolean,

    @Temporal(TemporalType.DATE)
    @Column(nullable = true)
    val programmedDateInitial: LocalDate,

    @Temporal(TemporalType.DATE)
    @Column(nullable = true)
    val programmedDateFinal: LocalDate,

    @CreationTimestamp
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    val deletedAt: LocalDateTime? = null
)
