package io.eventstime.model

import jakarta.persistence.*

enum class AppClient {
    CLIENT,
    STAND,
    BACKOFFICE
}

@Entity
@Table(name = "user_tokens", schema = "public")
data class UserToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    val appClient: AppClient,

    val tokenFcm: String? = null,
    val refreshToken: String? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User
)