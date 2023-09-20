package io.eventstime.model

import java.io.InputStream

data class FileUpload(
    val fileName: String,
    val contentType: String,
    val size: Long,
    val inputStream: InputStream
)
