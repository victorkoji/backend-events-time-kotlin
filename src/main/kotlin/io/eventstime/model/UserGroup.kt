package io.eventstime.model

import jakarta.persistence.*

@Entity
@Table(name = "user_groups", schema = "public")
class UserGroup(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val name: String
)
