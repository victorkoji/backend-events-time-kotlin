package io.eventstime.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.annotations.Where
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(name = "user_event_stands")
@IdClass(UserEventStandId::class)
@SQLDelete(sql = "UPDATE user_event_stands SET deleted_at=NOW() WHERE id = ?")
@Where(clause = "deleted_at is NULL")
data class UserEventStand(
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    val event: Event,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stand_id")
    val stand: Stand,

    val isResponsible: Boolean = false,

    @CreationTimestamp
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    val deletedAt: LocalDateTime? = null
)

class UserEventStandId(
    val user: User? = null,
    val event: Event? = null,
    val stand: Stand? = null
) : Serializable
