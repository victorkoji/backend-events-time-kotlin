package io.eventstime.utils

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class HashUtilsTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: HashUtils

    @Test
    fun `Check if password is equal`() {
        // GIVEN
        val password = "test"
        val hash = createHashBcrypt(password)

        // WHEN
        val result = testObject.checkBcrypt(password, hash)

        // THEN
        assertTrue(result)
    }

    @Test
    fun `Check if password is not equal`() {
        // GIVEN
        val password = "test"
        val hash = createHashBcrypt("test123")

        // WHEN
        val result = testObject.checkBcrypt(password, hash)

        // THEN
        assertFalse(result)
    }

    @Test
    fun `Create password hash`() {
        // GIVEN
        val password = "test"

        // WHEN
        val result = testObject.createHashBcrypt(password)

        // THEN
        assertTrue(testObject.checkBcrypt(password, result))
    }

    private fun createHashBcrypt(input: String): String {
        return BCrypt.hashpw(input, BCrypt.gensalt(10))
    }
}
