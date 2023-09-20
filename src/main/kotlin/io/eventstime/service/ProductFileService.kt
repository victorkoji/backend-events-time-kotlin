package io.eventstime.service

import io.eventstime.model.FileUpload
import io.eventstime.model.ProductFile
import io.eventstime.repository.ProductFileRepository
import io.eventstime.utils.HashUtils
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream

@Service
class ProductFileService(
    private val productFileRepository: ProductFileRepository,
    private val s3FileService: S3FileService,
    private val hashUtils: HashUtils
) {
    fun findById(productFileId: Long): ProductFile? {
        return productFileRepository.findByIdOrNull(productFileId)
    }

    fun createProductFile(productFileId: Long?, file: MultipartFile): ProductFile {
        deleteProductFile(productFileId)

        val fileUpload = createFileUpload(file)
        val s3Path = s3FileService.uploadFile(fileUpload)

        return productFileRepository.saveAndFlush(
            ProductFile(
                filename = fileUpload.fileName,
                filenameOriginal = file.originalFilename!!,
                mediaType = file.contentType!!,
                filepath = s3Path
            )
        )
    }

    fun deleteProductFile(productFileId: Long?) {
        if (productFileId != null) {
            findById(productFileId)?.let {
                productFileRepository.delete(it)
                s3FileService.deleteFile(it.filename)
            }
        }
    }

    private fun createFileUpload(file: MultipartFile): FileUpload {
        val filenameOriginal = StringUtils.cleanPath(file.originalFilename!!)
        val extension = filenameOriginal.substringAfterLast(".", "")
        val filenameHash = "${hashUtils.generateUniqueFileName()}.$extension"

        val fileContent = file.inputStream.use { it.readAllBytes() }

        return FileUpload(
            fileName = filenameHash,
            contentType = file.contentType!!,
            size = fileContent.size.toLong(),
            inputStream = ByteArrayInputStream(fileContent)
        )
    }
}
