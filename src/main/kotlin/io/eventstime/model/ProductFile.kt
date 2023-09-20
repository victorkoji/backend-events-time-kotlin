package io.eventstime.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.annotations.Where
import java.time.LocalDateTime

@Entity
@Table(name = "product_files")
@SQLDelete(sql = "UPDATE product_files SET deleted_at=NOW() WHERE id = ?")
@Where(clause = "deleted_at is NULL")
data class ProductFile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = true)
    val id: Long? = null,
    val filename: String,
    val filenameOriginal: String,
    val mediaType: String,
    val filepath: String,

    @CreationTimestamp
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    val deletedAt: LocalDateTime? = null
)
