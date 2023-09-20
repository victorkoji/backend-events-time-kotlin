package io.eventstime.service

import io.eventstime.model.FileUpload
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception

@Service
class S3FileService(
    private val s3Client: S3Client,

    @Value("\${events-time.s3.bucket}")
    private val bucketName: String,

    @Value("\${cloud.aws.s3.endpoint}")
    private val endpointS3: String
) {

    fun uploadFile(fileUpload: FileUpload): String {
        val key = fileUpload.fileName

        try {
            val objectRequest: PutObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .acl("public-read")
                .contentType(fileUpload.contentType)
                .build()

            s3Client.putObject(objectRequest, RequestBody.fromInputStream(fileUpload.inputStream, fileUpload.size))

            log.info("File ${fileUpload.fileName} uploaded successfully to ${key.toBucketPath()}.")

            return key.toBucketPath()
        } catch (e: S3Exception) {
            log.error("Cannot upload file to s3. Bucket $bucketName, Key: $key. Reason: ${e.message}")
            throw e
        }
    }

    fun deleteFile(filename: String) {
        try {
            val deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .build()

            s3Client.deleteObject(deleteObjectRequest)

            log.info("File $filename deleted successfully from s3 bucket.")
        } catch (e: Exception) {
            log.error("Cannot delete file $filename from s3 bucket. Reason: ${e.message}")
            throw e
        }
    }

    private fun String.toBucketPath() = "$endpointS3/$bucketName/$this"

    companion object {
        val log: Logger = LoggerFactory.getLogger(S3FileService::class.java)
    }
}
