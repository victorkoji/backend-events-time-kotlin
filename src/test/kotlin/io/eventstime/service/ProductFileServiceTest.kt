package io.eventstime.service

import io.eventstime.model.ProductFile
import io.eventstime.repository.ProductFileRepository
import io.eventstime.utils.HashUtils
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class ProductFileServiceTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: ProductFileService

    private val productFileRepository = mockk<ProductFileRepository>()
    private val s3FileService = mockk<S3FileService>(relaxed = true)
    private val hashUtils = mockk<HashUtils>()

    private val productFile = ProductFile(
        id = 1,
        filename = "filename.jpg",
        filenameOriginal = "filenameOriginal",
        mediaType = "mediaType",
        filepath = "filepath"
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        every {
            hashUtils.generateUniqueFileName()
        } returns "1234"
    }

    @Test
    fun `Find product file by id return object`() {
        // GIVEN
        every {
            productFileRepository.findByIdOrNull(productFile.id)
        } returns productFile

        // WHEN
        val result = testObject.findById(productFile.id!!)

        // THEN
        assertEquals(productFile.filename, result?.filename)
        assertEquals(productFile.filenameOriginal, result?.filenameOriginal)
        assertEquals(productFile.mediaType, result?.mediaType)
        assertEquals(productFile.filepath, result?.filepath)
    }

    @Test
    fun `Find product file by id return empty`() {
        // GIVEN
        every {
            productFileRepository.findByIdOrNull(productFile.id)
        } returns null

        // WHEN
        val result = testObject.findById(productFile.id!!)

        // THEN
        assertNull(result)
        verify(exactly = 1) { productFileRepository.findByIdOrNull(productFile.id) }
    }

    @Test
    fun `Create product file with success`() {
        // GIVEN
        val fileName = productFile.filename
        val contentType = productFile.mediaType
        val content = "Example file contents.".toByteArray()
        val file = MockMultipartFile(fileName, fileName, contentType, content)

        every {
            productFileRepository.findByIdOrNull(productFile.id!!)
        } returns productFile

        every {
            productFileRepository.delete(productFile)
        } just runs

        every {
            s3FileService.deleteFile(fileName)
        } just runs

        every {
            s3FileService.uploadFile(any())
        } returns "s3Path"

        every {
            productFileRepository.saveAndFlush(
                productFile.copy(
                    id = null,
                    filename = "1234.jpg",
                    filenameOriginal = "filename.jpg",
                    filepath = "s3Path"
                )
            )
        } returns productFile

        // WHEN
        val result = testObject.createProductFile(productFile.id!!, file)

        // THEN
        assertEquals(fileName, result.filename)
        assertEquals(contentType, result.mediaType)
        assertEquals(productFile.filepath, result.filepath)
    }
}
