package io.eventstime.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.annotations.Where
import java.time.LocalDateTime

@Entity
@Table(name = "products")
@SQLDelete(sql = "UPDATE products SET deleted_at=NOW() WHERE id = ?")
@Where(clause = "deleted_at is NULL")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = true)
    val id: Long? = null,
    val name: String,
    val price: Float,
    val customFormTemplate: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_category_id")
    val productCategory: ProductCategory? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stand_id")
    val stand: Stand? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_file_id")
    val productFile: ProductFile? = null,

    @CreationTimestamp
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    val deletedAt: LocalDateTime? = null
)
