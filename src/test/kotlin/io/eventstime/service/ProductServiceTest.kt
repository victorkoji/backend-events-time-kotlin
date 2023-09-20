package io.eventstime.service

import io.eventstime.exception.*
import io.eventstime.model.*
import io.eventstime.repository.ProductRepository
import io.eventstime.schema.ProductRequest
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class ProductServiceTest {
    @InjectMockKs(injectImmutable = true)
    lateinit var testObject: ProductService

    private val productRepository = mockk<ProductRepository>()
    private val standService = mockk<StandService>()
    private val productCategoryService = mockk<ProductCategoryService>()
    private val productFileService = mockk<ProductFileService>()

    private val product = Product(
        id = 1,
        name = "product-1",
        price = 2F,
        customFormTemplate = null,
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
    fun `Find all products list not empty`() {
        // GIVEN
        every {
            productRepository.findAll()
        } returns listOf(product)

        // WHEN
        val result = testObject.findAll()

        // THEN
        assertEquals(1, result.size)
        assertEquals(1, product.id)

        verify(exactly = 1) { productRepository.findAll() }
    }

    @Test
    fun `Find all products list empty`() {
        // GIVEN
        every {
            productRepository.findAll()
        } returns emptyList()

        // WHEN
        val result = testObject.findAll()

        // THEN
        assertEquals(0, result.size)
        verify(exactly = 1) { productRepository.findAll() }
    }

    @Test
    fun `Find product by id return object product`() {
        // GIVEN
        every {
            productRepository.findById(product.id!!)
        } returns Optional.of(product)

        // WHEN
        val result = testObject.findById(product.id!!)

        // THEN
        assertNotNull(result)

        verify(exactly = 1) { productRepository.findById(product.id!!) }
    }

    @Test
    fun `Find product by id return null`() {
        // GIVEN
        val standCategoryId = 1L

        every {
            productRepository.findById(standCategoryId)
        } returns Optional.empty()

        // WHEN
        val result = testObject.findById(standCategoryId)

        // THEN
        assertNull(result)
        verify(exactly = 1) { productRepository.findById(standCategoryId) }
    }

    @Test
    fun `Create product with success`() {
        // GIVEN
        val productRequest = ProductRequest(
            name = product.name,
            price = product.price,
            customFormTemplate = product.customFormTemplate,
            standId = product.stand?.id!!,
            productCategoryId = product.productCategory?.id!!
        )

        every {
            standService.findById(product.stand?.id!!)
        } returns product.stand

        every {
            productCategoryService.findById(product.productCategory?.id!!)
        } returns product.productCategory

        every {
            productRepository.saveAndFlush(product.copy(id = null))
        } returns product

        // WHEN
        val result = testObject.createProduct(productRequest)

        // THEN
        assertNotNull(result)
        verify(exactly = 1) { productRepository.saveAndFlush(product.copy(id = null)) }
    }

    @Test
    fun `Create product with stand not found error `() {
        // GIVEN
        val productRequest = ProductRequest(
            name = product.name,
            price = product.price,
            customFormTemplate = product.customFormTemplate,
            standId = product.stand?.id!!,
            productCategoryId = product.productCategory?.id!!
        )

        every {
            standService.findById(product.stand?.id!!)
        } returns null

        every {
            productCategoryService.findById(product.productCategory?.id!!)
        } returns product.productCategory

        // WHEN
        val result = assertThrows<CustomException> { testObject.createProduct(productRequest) }

        // THEN
        assertEquals(StandErrorType.STAND_NOT_FOUND.name, result.message)
        verify(exactly = 0) { productRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Create product with product category not found error `() {
        // GIVEN
        val productRequest = ProductRequest(
            name = product.name,
            price = product.price,
            customFormTemplate = product.customFormTemplate,
            standId = product.stand?.id!!,
            productCategoryId = product.productCategory?.id!!
        )

        every {
            standService.findById(product.stand?.id!!)
        } returns product.stand

        every {
            productCategoryService.findById(product.productCategory?.id!!)
        } returns null

        // WHEN
        val result = assertThrows<CustomException> { testObject.createProduct(productRequest) }

        // THEN
        assertEquals(ProductCategoryErrorType.PRODUCT_CATEGORY_NOT_FOUND.name, result.message)
        verify(exactly = 0) { productRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Create product with error to save`() {
        // GIVEN
        val productRequest = ProductRequest(
            name = product.name,
            price = product.price,
            customFormTemplate = product.customFormTemplate,
            standId = product.stand?.id!!,
            productCategoryId = product.productCategory?.id!!
        )

        every {
            standService.findById(product.stand?.id!!)
        } returns product.stand

        every {
            productCategoryService.findById(product.productCategory?.id!!)
        } returns product.productCategory

        every {
            productRepository.saveAndFlush(any())
        } throws Exception("Failure to save")

        // WHEN
        val result = assertThrows<Exception> { testObject.createProduct(productRequest) }

        // THEN
        assertEquals("Failure to save", result.message)
        verify(exactly = 1) { productRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Update product with success`() {
        // GIVEN
        val productRequest = ProductRequest(
            name = product.name,
            price = product.price,
            customFormTemplate = product.customFormTemplate,
            standId = product.stand?.id!!,
            productCategoryId = product.productCategory?.id!!
        )

        every {
            standService.findById(product.stand?.id!!)
        } returns product.stand

        every {
            productCategoryService.findById(product.productCategory?.id!!)
        } returns product.productCategory

        every {
            productRepository.findById(product.id!!)
        } returns Optional.of(product)

        every {
            productRepository.saveAndFlush(product)
        } returns product

        // WHEN
        val result = testObject.updateProduct(product.id!!, productRequest)

        // THEN
        assertNotNull(result)
        verify(exactly = 1) { productRepository.saveAndFlush(product) }
    }

    @Test
    fun `Update product with error product not found`() {
        // GIVEN
        val productRequest = ProductRequest(
            name = product.name,
            price = product.price,
            customFormTemplate = product.customFormTemplate,
            standId = product.stand?.id!!,
            productCategoryId = product.productCategory?.id!!
        )

        every {
            standService.findById(product.stand?.id!!)
        } returns product.stand

        every {
            productCategoryService.findById(product.productCategory?.id!!)
        } returns product.productCategory

        every {
            productRepository.findById(product.id!!)
        } returns Optional.empty()

        every {
            productRepository.saveAndFlush(product)
        } returns product

        // WHEN
        val result = assertThrows<CustomException> {
            testObject.updateProduct(product.id!!, productRequest)
        }

        // THEN
        assertEquals(ProductErrorType.PRODUCT_NOT_FOUND.name, result.message)
        verify(exactly = 0) { productRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Update product with stand not found error`() {
        // GIVEN
        val productRequest = ProductRequest(
            name = product.name,
            price = product.price,
            customFormTemplate = product.customFormTemplate,
            standId = product.stand?.id!!,
            productCategoryId = product.productCategory?.id!!
        )

        every {
            standService.findById(product.stand?.id!!)
        } returns null

        every {
            productCategoryService.findById(product.productCategory?.id!!)
        } returns product.productCategory

        // WHEN
        val result = assertThrows<CustomException> {
            testObject.updateProduct(product.id!!, productRequest)
        }

        // THEN
        assertEquals(StandErrorType.STAND_NOT_FOUND.name, result.message)
        verify(exactly = 0) { productRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Update product with product category not found error`() {
        // GIVEN
        val productRequest = ProductRequest(
            name = product.name,
            price = product.price,
            customFormTemplate = product.customFormTemplate,
            standId = product.stand?.id!!,
            productCategoryId = product.productCategory?.id!!
        )

        every {
            standService.findById(product.stand?.id!!)
        } returns product.stand

        every {
            productCategoryService.findById(product.productCategory?.id!!)
        } returns null

        // WHEN
        val result = assertThrows<CustomException> {
            testObject.updateProduct(product.id!!, productRequest)
        }

        // THEN
        assertEquals(ProductCategoryErrorType.PRODUCT_CATEGORY_NOT_FOUND.name, result.message)
        verify(exactly = 0) { productRepository.saveAndFlush(any()) }
    }

    @Test
    fun `Delete product with success`() {
        // GIVEN
        every {
            productRepository.findById(product.id!!)
        } returns Optional.of(product)

        every {
            productRepository.delete(product)
        } just runs

        // WHEN
        val result = testObject.deleteProduct(product.id!!)

        // THEN
        assertNotNull(result)
        verify(exactly = 1) { productRepository.delete(product) }
    }

    @Test
    fun `Delete product with error product not found`() {
        // GIVEN
        every {
            productRepository.findById(product.id!!)
        } returns Optional.empty()

        every {
            productRepository.delete(product)
        } just runs

        // WHEN
        val result = assertThrows<CustomException> { testObject.deleteProduct(product.id!!) }

        // THEN
        assertEquals(ProductErrorType.PRODUCT_NOT_FOUND.name, result.message)
        verify(exactly = 0) { productRepository.delete(any()) }
    }

    @Test
    fun `Upload image to product with success`() {
        // GIVEN
        val fileName = "example.txt"
        val contentType = "text/plain"
        val content = "Example file contents.".toByteArray()
        val file = MockMultipartFile(fileName, fileName, contentType, content)

        val productFile = ProductFile(
            id = 1,
            filename = "filename",
            filenameOriginal = "filenameOriginal",
            mediaType = "mediaType",
            filepath = "filepath"
        )

        every {
            productRepository.findById(product.id!!)
        } returns Optional.of(product)

        every {
            productFileService.createProductFile(product.productFile?.id, file)
        } returns productFile

        every {
            productRepository.saveAndFlush(product.copy(productFile = productFile))
        } returns product

        // WHEN
        val result = testObject.uploadImage(product.id!!, file)

        // THEN
        assertNotNull(result)
        verify(exactly = 1) { productRepository.saveAndFlush(product.copy(productFile = productFile)) }
    }

    @Test
    fun `Upload image to product with error product not found`() {
        // GIVEN
        val fileName = "example.txt"
        val contentType = "text/plain"
        val content = "Example file contents.".toByteArray()
        val file = MockMultipartFile(fileName, fileName, contentType, content)

        val productFile = ProductFile(
            id = 1,
            filename = "filename",
            filenameOriginal = "filenameOriginal",
            mediaType = "mediaType",
            filepath = "filepath"
        )

        every {
            productRepository.findById(product.id!!)
        } returns Optional.empty()

        every {
            productFileService.createProductFile(product.productFile?.id, file)
        } returns productFile

        every {
            productRepository.saveAndFlush(product.copy(productFile = productFile))
        } returns product

        // WHEN
        val result = assertThrows<CustomException> { testObject.uploadImage(product.id!!, file) }

        // THEN
        assertEquals(ProductErrorType.PRODUCT_NOT_FOUND.name, result.message)
        verify(exactly = 0) { productRepository.saveAndFlush(any()) }
    }
}
