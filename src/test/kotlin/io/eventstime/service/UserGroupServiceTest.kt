package io.eventstime.service

import io.eventstime.model.UserGroup
import io.eventstime.repository.UserGroupRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class UserGroupServiceTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: UserGroupService

    private val userGroupRepository = mockk<UserGroupRepository>()

    @Test
    fun `Find by id with success`() {
        // GIVEN
        val userGroupId = 1L
        val userGroup = UserGroup(id = 1, name = "Test")

        every {
            userGroupRepository.findByIdOrNull(userGroupId)
        } returns userGroup

        // WHEN
        val result = testObject.findById(userGroupId)

        // THEN
        assertEquals(userGroup, result)
        verify(exactly = 1) { userGroupRepository.findByIdOrNull(userGroupId) }
    }

    @Test
    fun `Find by id returns null`() {
        // GIVEN
        val userGroupId = 1L

        every {
            userGroupRepository.findByIdOrNull(userGroupId)
        } returns null

        // WHEN
        val result = testObject.findById(userGroupId)

        // THEN
        assertNull(result)
        verify(exactly = 1) { userGroupRepository.findByIdOrNull(userGroupId) }
    }
}
