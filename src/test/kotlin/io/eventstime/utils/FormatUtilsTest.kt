package io.eventstime.utils

import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class FormatUtilsTest {
    @Test
    fun `Format string with removing accents`() {
        // GIVEN
        val name = "João da Silva Pão"

        // WHEN
        val nameWithoutAccents = name.removeAccents()

        // THEN
        assertEquals(
            "Joao da Silva Pao",
            nameWithoutAccents
        )
    }
}
