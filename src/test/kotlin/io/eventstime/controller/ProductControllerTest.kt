package io.eventstime.controller

import io.eventstime.exception.*
import io.eventstime.model.*
import io.eventstime.schema.ProductRequest
import io.eventstime.service.ProductService
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class ProductControllerTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: ProductController

    private val productService = mockk<ProductService>()

    private val product = Product(
        id = 1,
        name = "product-1",
        price = 2F,
        productCategory = ProductCategory(
            id = 1,
            name = "product category"
        ),
        stand = Stand(
            id = 1,
            name = "stand",
            isCashier = true
        )
    )

    @Test
    fun `Find all product with list not empty`() {
        // GIVEN
        every {
            productService.findAll()
        } returns listOf(product)

        // WHEN
        val result = testObject.findAllProduct()

        // THEN
        Assertions.assertEquals(1, result.size)
        verify(exactly = 1) { productService.findAll() }
    }

    @Test
    fun `Find all product with list empty`() {
        // GIVEN
        every {
            productService.findAll()
        } returns emptyList()

        // WHEN
        val result = testObject.findAllProduct()

        // THEN
        Assertions.assertEquals(0, result.size)
        verify(exactly = 1) { productService.findAll() }
    }

    @Test
    fun `Create product with success`() {
        // GIVEN
        val productRequest = ProductRequest(
            name = product.name,
            price = 2F,
            customFormTemplate = null,
            standId = product.stand?.id!!,
            productCategoryId = product.productCategory?.id!!
        )

        every {
            productService.createProduct(productRequest)
        } returns product

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.createProduct(productRequest)
        }

        // THEN
        verify(exactly = 1) { productService.createProduct(productRequest) }
    }

    @Test
    fun `Update product with success`() {
        // GIVEN
        val productRequest = ProductRequest(
            name = product.name,
            price = 2F,
            customFormTemplate = null,
            standId = product.stand?.id!!,
            productCategoryId = product.productCategory?.id!!
        )

        every {
            productService.updateProduct(product.id!!, productRequest)
        } returns product

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.updateProduct(product.id!!, productRequest)
        }

        // THEN
        verify(exactly = 1) { productService.updateProduct(product.id!!, productRequest) }
    }

    @Test
    fun `Delete product with success`() {
        // GIVEN
        every {
            productService.deleteProduct(product.id!!)
        } just runs

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.deleteProduct(product.id!!)
        }

        // THEN
        verify(exactly = 1) { productService.deleteProduct(product.id!!) }
    }

    @Test
    fun `Find product with success`() {
        // GIVEN
        every {
            productService.findById(product.id!!)
        } returns product

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.findProduct(product.id!!)
        }

        // THEN
        verify(exactly = 1) { productService.findById(product.id!!) }
    }

    @Test
    fun `Upload image in product with success`() {
        // GIVEN
        val fileName = "example.txt"
        val contentType = "text/plain"
        val content = "Example file contents.".toByteArray()
        val file = MockMultipartFile(fileName, fileName, contentType, content)

        every {
            productService.uploadImage(product.id!!, file)
        } returns product

        // WHEN
        Assertions.assertDoesNotThrow {
            testObject.uploadImageProduct(product.id!!, file)
        }

        // THEN
        verify(exactly = 1) { productService.uploadImage(product.id!!, file) }
    }

    @Nested
    inner class HandleErrorTest {

        @Test
        fun `Handle error NOT_FOUND`() {
            // GIVEN
            val exceptionProductNotFound = CustomException(ProductErrorType.PRODUCT_NOT_FOUND)
            val exceptionProductCategoryNotFound = CustomException(ProductCategoryErrorType.PRODUCT_CATEGORY_NOT_FOUND)
            val exceptionStandNotFound = CustomException(StandErrorType.STAND_NOT_FOUND)

            // WHEN
            val resultProductNotFound = testObject.handleException(exceptionProductNotFound)
            val resultProductCategoryNotFound = testObject.handleException(exceptionProductCategoryNotFound)
            val resultStandNotFound = testObject.handleException(exceptionStandNotFound)

            // THEN
            Assertions.assertEquals(HttpStatus.NOT_FOUND, resultProductNotFound.statusCode)
            Assertions.assertEquals(HttpStatus.NOT_FOUND, resultProductCategoryNotFound.statusCode)
            Assertions.assertEquals(HttpStatus.NOT_FOUND, resultStandNotFound.statusCode)
        }

        @Test
        fun `Handle error BAD_REQUEST`() {
            // GIVEN
            val exception = CustomException()

            // WHEN
            val result = testObject.handleException(exception)

            // THEN
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        }

        @Test
        fun `Handle error generic exception`() {
            // GIVEN
            val exception = Exception("error")

            // WHEN
            val result = testObject.handleException(exception)

            // THEN
            Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.statusCode)
        }
    }
}
