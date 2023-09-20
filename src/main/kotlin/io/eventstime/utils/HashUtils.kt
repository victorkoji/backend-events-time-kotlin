package io.eventstime.utils

import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class HashUtils {

    fun checkBcrypt(input: String, hash: String): Boolean {
        return BCrypt.checkpw(input, hash)
    }

    fun createHashBcrypt(input: String): String {
        return BCrypt.hashpw(input, BCrypt.gensalt(10))
    }

    fun generateUniqueFileName() = UUID.randomUUID().toString()
}
