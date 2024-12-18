package io.eventstime.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.annotations.Where
import java.time.LocalDateTime

@Entity
@Table(name = "product_categories")
@SQLDelete(sql = "UPDATE product_categories SET deleted_at=NOW() WHERE id = ?")
@Where(clause = "deleted_at is NULL")
data class ProductCategory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = true)
    val id: Long? = null,
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    val event: Event? = null,

    @OneToMany(mappedBy = "productCategory")
    val products: List<Product>? = null,

    @CreationTimestamp
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    val deletedAt: LocalDateTime? = null
)
