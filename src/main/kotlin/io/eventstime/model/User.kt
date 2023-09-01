package io.eventstime.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import java.util.Date

@Entity
@Table(name = "users", schema = "public")
@SQLDelete(sql = "UPDATE users SET deleted_at=NOW() WHERE id = ?")
@Where(clause = "deleted_at is NULL")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = true)
    val id: Long? = null,
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDateTime,
    val email: String,
    val cellphone: String,
    val password: String,
    val tokenFcm: String,

    @Temporal(TemporalType.TIMESTAMP)
    val createdAt: Date? = null,

    @Temporal(TemporalType.TIMESTAMP)
    val updatedAt: Date? = null,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    val deletedAt: Date? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_group_id")
    val userGroup: UserGroup? = null
)
