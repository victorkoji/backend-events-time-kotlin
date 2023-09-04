package io.eventstime.auth

import io.eventstime.exception.AuthErrorType
import io.eventstime.exception.CustomException
import io.eventstime.model.UserAuth
import io.eventstime.utils.HashUtils
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import java.util.*
@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class AuthorizationServiceTest {

    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: AuthorizationService

    private val hashUtils = mockk<HashUtils>()

    private val user = UserAuth(1, "firstName", "lastName", "email@teste.com", 1)

    private fun setUserToken() {
        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
            user,
            "",
            Collections.emptyList()
        )
    }

    private fun setWithoutToken() {
        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
            null,
            "",
            Collections.emptyList()
        )
    }

    @Test
    fun `Get user with success`() {
        // GIVEN
        setUserToken()

        // WHEN
        val userResult = testObject.getUser()

        // THEN
        assertEquals(user, userResult)
    }

    @Test
    fun `Get user throw exception`() {
        // GIVEN
        setWithoutToken()

        // WHEN
        val exception = assertThrows<CustomException> {
            testObject.getUser()
        }

        // THEN
        assertEquals(AuthErrorType.UNAUTHORIZED.name, exception.message)
    }

    @Test
    fun `Password is equal`() {
        // GIVEN
        val password = "password"
        val passwordSaved = "password"

        every {
            hashUtils.checkBcrypt(password, passwordSaved)
        } returns true

        // WHEN
        val result = testObject.checkIsValidPassword(password, passwordSaved)

        // THEN
        assertTrue(result)
    }

    @Test
    fun `Password is not equal`() {
        // GIVEN
        val password = "password"
        val passwordSaved = "passwordSaved"

        every {
            hashUtils.checkBcrypt(password, passwordSaved)
        } returns false

        // when
        val result = testObject.checkIsValidPassword(password, passwordSaved)

        // THEN
        assertFalse(result)
    }
}
