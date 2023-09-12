package io.eventstime.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.annotations.Where
import java.time.LocalDateTime

@Entity
@Table(name = "stands")
@SQLDelete(sql = "UPDATE stands SET deleted_at=NOW() WHERE id = ?")
@Where(clause = "deleted_at is NULL")
data class Stand(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = true)
    val id: Long? = null,

    val name: String,
    val isCashier: Boolean,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    val event: Event? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stand_category_id")
    val standCategory: StandCategory? = null,

    @CreationTimestamp
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    val deletedAt: LocalDateTime? = null
)
