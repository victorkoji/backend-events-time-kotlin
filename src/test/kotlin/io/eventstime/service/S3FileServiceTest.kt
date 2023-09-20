package io.eventstime.service

import io.eventstime.model.FileUpload
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class S3FileServiceTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: S3FileService

    private val s3Client = mockk<S3Client>(relaxed = true)

    private val bucketName = "events-time-bucket-test"
    private val endpointS3 = "http://localhost:8080"

    @Test
    fun `Upload file in s3 with success`() {
        // GIVEN
        val file =
            MockMultipartFile("foo", "foo.bin", MediaType.IMAGE_PNG_VALUE, "Hello World".toByteArray())

        val fileUpload = FileUpload(
            file.originalFilename,
            file.contentType!!,
            file.size,
            file.inputStream
        )

        val objectRequest: PutObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileUpload.fileName)
            .acl("public-read")
            .contentType(fileUpload.contentType)
            .build()

        every {
            s3Client.putObject(
                objectRequest,
                RequestBody.fromInputStream(fileUpload.inputStream, fileUpload.size)
            )
        } returns null

        // WHEN
        val result = testObject.uploadFile(fileUpload)

        // THEN
        assertEquals(fileUpload.fileName.toBucketPath(), result)
    }

    @Test
    fun `Upload file in s3 with exception`() {
        // GIVEN
        val file =
            MockMultipartFile("foo", "foo.bin", MediaType.IMAGE_PNG_VALUE, "Hello World".toByteArray())

        val fileUpload = FileUpload(
            file.originalFilename,
            file.contentType!!,
            file.size,
            file.inputStream
        )

        every {
            s3Client.putObject(
                any<PutObjectRequest>(),
                any<RequestBody>()
            )
        } throws S3Exception.builder().message("test").build()

        // WHEN
        val result = assertThrows<S3Exception> {
            testObject.uploadFile(fileUpload)
        }

        // THEN
        assertEquals("test", result.message)
    }

    @Test
    fun `delete file in s3 with success`() {
        // GIVEN
        val filename = "filename"
        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(filename)
            .build()

        every {
            s3Client.deleteObject(deleteObjectRequest)
        } returns null

        // WHEN | THEN
        assertDoesNotThrow {
            testObject.deleteFile(filename)
        }
    }

    @Test
    fun `Delete file in s3 with exception`() {
        // GIVEN
        val filename = "filename"

        every {
            s3Client.deleteObject(any<DeleteObjectRequest>())
        } throws S3Exception.builder().message("test").build()

        // WHEN
        val result = assertThrows<S3Exception> {
            testObject.deleteFile(filename)
        }

        // THEN
        assertEquals("test", result.message)
    }

    private fun String.toBucketPath() = "$endpointS3/$bucketName/$this"
}
