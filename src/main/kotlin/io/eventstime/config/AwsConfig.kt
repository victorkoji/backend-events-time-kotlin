package io.eventstime.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

@Configuration
class AwsConfig(
    @Value("\${cloud.aws.region.static}")
    private val region: String,

    @Value("\${cloud.aws.s3.endpoint}")
    private val endpoint: String,

    @Value("\${cloud.aws.credentials.access-key}")
    val accessKey: String,

    @Value("\${cloud.aws.credentials.secret-key}")
    val secretKey: String
) {
    @Bean
    fun s3Client(): S3Client {
        val builder = S3Client.builder().region(Region.of(region))

        if (accessKey.isNotBlank() && secretKey.isNotBlank()) {
            val awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey)
            builder.credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
        }

        if (endpoint.isNotBlank()) {
            builder.endpointOverride(URI.create(endpoint))
        }
        return builder.build()
    }
}
