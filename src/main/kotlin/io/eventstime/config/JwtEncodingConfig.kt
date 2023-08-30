package io.eventstime.config

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import javax.crypto.spec.SecretKeySpec

@Configuration
class JwtEncodingConfig(
    @Value("\${jwt.access-token.secret-key}")
    private val jwtKeyAccessToken: String,

    @Value("\${jwt.refresh-token.secret-key}")
    private val jwtKeyRefreshToken: String
) {
    private val secretKeyAccessToken = SecretKeySpec(jwtKeyAccessToken.toByteArray(), "HmacSHA256")
    private val secretKeyRefreshToken = SecretKeySpec(jwtKeyRefreshToken.toByteArray(), "HmacSHA256")

    @Bean("jwtDecoderAccessToken")
    @Primary
    fun jwtDecoderAccessToken(): JwtDecoder {
        return NimbusJwtDecoder.withSecretKey(secretKeyAccessToken).build()
    }

    @Bean("jwtEncoderAccessToken")
    @Primary
    fun jwtEncoderAccessToken(): JwtEncoder {
        val secret = ImmutableSecret<SecurityContext>(secretKeyAccessToken)
        return NimbusJwtEncoder(secret)
    }

    @Bean("jwtDecoderRefreshToken")
    fun jwtDecoderRefreshToken(): JwtDecoder {
        return NimbusJwtDecoder.withSecretKey(secretKeyRefreshToken).build()
    }

    @Bean("jwtEncoderRefreshToken")
    fun jwtEncoderRefreshToken(): JwtEncoder {
        val secret = ImmutableSecret<SecurityContext>(secretKeyRefreshToken)
        return NimbusJwtEncoder(secret)
    }
}
