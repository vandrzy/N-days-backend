# Unit Test

Unit Testing adalah pengujian unit terkecil dari kode (biasanya method di Service) secara terisolasi, tanpa bergantung ke:
- Database
- API eksternal
- Framework lain


## Peran JUnit & Mockito

- JUnit
  - Menjalankan test
  - Assertion (assertEquals, assertThrows, dll)
  - JUnit 5 (Jupiter) adalah standar saat ini.

- Mockito
  - Mock dependency (Repository, Client API, dll)
  - Mengontrol return value dependency
  - Memverifikasi method dipanggil atau tidak
  - Mockito membuat test benar-benar unit test, bukan integration test.

## Upload Dependencies
```kotlin
testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")	
testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
```

## Rangkuman Fungsi Umum Unit Testing (JUnit + Mockito)

###  JUnit (Assertion & Lifecycle)

| Fungsi | Kegunaan | Contoh |
|------|----------|--------|
| `@Test` | Menandai method sebagai unit test | `@Test fun testCreate()` |
| `@BeforeEach` | Dieksekusi sebelum setiap test | `@BeforeEach fun setup()` |
| `@AfterEach` | Dieksekusi setelah setiap test | `@AfterEach fun tearDown()` |
| `@BeforeAll` | Sekali sebelum semua test | `@BeforeAll fun init()` |
| `@AfterAll` | Sekali setelah semua test | `@AfterAll fun cleanup()` |
| `assertEquals()` | Bandingkan nilai | `assertEquals(200, result.code)` |
| `assertNotNull()` | Pastikan tidak null | `assertNotNull(response)` |
| `assertNull()` | Pastikan null | `assertNull(data)` |
| `assertTrue()` | Kondisi harus true | `assertTrue(list.isEmpty())` |
| `assertFalse()` | Kondisi harus false | `assertFalse(result.isError)` |
| `assertThrows()` | Menguji exception | `assertThrows<IllegalArgumentException> { service.create() }` |

---

###  Mockito (Mocking & Verification)

| Fungsi | Kegunaan | Contoh |
|------|----------|--------|
| `@Mock` | Membuat object tiruan | `@Mock lateinit var repo: Repo` |
| `@InjectMocks` | Inject mock ke class test | `@InjectMocks lateinit var service: Service` |
| `Mockito.when()` | Set perilaku mock | `when(repo.find()).thenReturn(data)` |
| ``Mockito.`when`()`` | Versi Kotlin (hindari keyword) | ``Mockito.`when`(repo.find())`` |
| `thenReturn()` | Return nilai | `thenReturn(user)` |
| `thenThrow()` | Lempar exception | `thenThrow(RuntimeException())` |
| `doNothing()` | Untuk method void | `doNothing().when(service).delete(id)` |
| `doThrow()` | Void method lempar error | `doThrow(Exception()).when(repo).delete(id)` |
| `verify()` | Pastikan method dipanggil | `verify(repo).save(any())` |
| `verify(..., times(n))` | Dipanggil n kali | `verify(repo, times(2)).findAll()` |
| `verify(..., never())` | Tidak boleh dipanggil | `verify(repo, never()).delete(any())` |
| `verifyNoInteractions()` | Tidak ada interaksi | `verifyNoInteractions(repo)` |
| `verifyNoMoreInteractions()` | Tidak ada interaksi tambahan | `verifyNoMoreInteractions(repo)` |

---

###  Argument Matcher

| Fungsi | Kegunaan | Contoh |
|------|----------|--------|
| `any()` | Argumen bebas | `any()` |
| `anyString()` | String apapun | `anyString()` |
| `anyLong()` | Long apapun | `anyLong()` |
| `eq()` | Cocokkan nilai spesifik | `eq("products")` |
| `argThat()` | Validasi custom | `argThat { it.size > 0 }` |

---

### Best Practice Singkat

| Situasi | Gunakan |
|-------|--------|
| Test normal flow | `assertEquals`, `verify()` |
| Test error flow | `assertThrows`, `never()` |
| Test side-effect | `verify()` |
| Hindari dependency asli | `@Mock` |
| Fokus 1 skenario | 1 test = 1 kondisi |


## Contoh Impementasi Unit Test
- Impementasi pada fungsi registrasi pada AuthService
```kotlin
@ExtendWith(MockitoExtension::class)
class AuthServiceTest {
    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var authenticationManager: AuthenticationManager

    @Mock
    lateinit var jwtService: JwtService

    @InjectMocks
    lateinit var authService: AuthService

    @Test
    fun `registration success test`(){
        val request = AuthRequest(
            "vandy", "12345", Role.ROLE_USER
        )

        whenever(userRepository.findByUsername("vandy"))
            .thenReturn(null)

        whenever(userRepository.save(any()))
            .thenAnswer{it.arguments[0]}

        val response = authService.registration(request)

        assertEquals("Registration Success", response.message)

        verify(userRepository, times(1)).save(any())
    }

    @Test
    fun `user already exist`(){
        val request = AuthRequest(
            "vandy", "12345", Role.ROLE_USER
        )

        whenever(userRepository.findByUsername("vandy"))
            .thenReturn(User("a","vandy", "12345", Role.ROLE_USER))

        val exeption = assertThrows<ResponseStatusException> {
            authService.registration(request)
        }

        assertEquals(org.springframework.http.HttpStatus.CONFLICT, exeption.statusCode)

        assertEquals("Username already exists", exeption.reason)

        verify(userRepository, never()).save(any())

    }
}
```
- Impementasi pada fungsi update pada ProductService
```kotlin
@ExtendWith(MockitoExtension::class)
class ProductServiceTest {

    @Mock
    lateinit var productRepository: ProductRepository

    @Mock
    lateinit var cloudinaryService: CloudinaryService

    @InjectMocks
    lateinit var productService: ProductService


    @Test
    fun `upload without image (createProduct())`(){
        val request = CreateProductRequest(
            title = "Macbook Pro"
        )

        whenever(productRepository.save(any()))
            .thenAnswer{it.arguments[0]}

        val response = productService.createProduct(request, null)

        assertEquals("Macbook Pro", response.title)

        assertNull(response.photoUrl)

        verify(cloudinaryService, never()).uploadImage(any(), any())
        verify(productRepository, times(1)).save(any())
    }

    @Test
    fun `upload with image (createProduct())`(){
        val request = CreateProductRequest(
            title = "Macbook Pro"
        )
        val file = mock<MultipartFile>()
        whenever(file.isEmpty).thenReturn(false)
        whenever(file.contentType).thenReturn("image/png")
        whenever(file.size).thenReturn(1024)

        val uploadResponse = CloudinaryUploadResponse(
            publicId = "product-123",
            secureUrl = "https://cloudinary.com/product.png"
        )

        whenever(cloudinaryService.uploadImage(file, "product"))
            .thenReturn(uploadResponse)

        whenever(productRepository.save(any()))
            .thenAnswer{it.arguments[0]}

        val result = productService.createProduct(request,file)

        assertEquals("Macbook Pro", result.title)
        assertEquals("https://cloudinary.com/product.png", result.photoUrl)

        verify(cloudinaryService, times(1))
            .uploadImage(file, "product")

        verify(productRepository, times(1))
            .save(any())

    }

    @Test
    fun `upload image ada foto lama dan baru (uploadImage())`(){
        val oldProduct = Product(
            id = "product-1",
            shortCode = "PRD-1",
            title = "Old title",
            photoId = "old-photo-id",
            photoUrl = "old-photo-url"
        )

        whenever(productRepository.findByShortCode("PRD-1"))
            .thenReturn(oldProduct)

        val file = mock<MultipartFile>()
        whenever(file.isEmpty).thenReturn(false)
        whenever(file.contentType).thenReturn("image/png")

        val uploadResponse = CloudinaryUploadResponse(
            publicId = "new-photo-id",
            secureUrl = "new-photo-url"
        )

        whenever(cloudinaryService.uploadImage(file, "products"))
            .thenReturn(uploadResponse)


        val request = UpdateProductRequest(
            "New title"
        )

        val result = productService.updateProduct("PRD-1", request, file)

        assertEquals("New title", result.title)
        assertEquals("new-photo-url", result.photoUrl)

        verify(cloudinaryService).deleteImage("old-photo-id")
        verify(cloudinaryService).uploadImage(file, "products")
    }
}
```


