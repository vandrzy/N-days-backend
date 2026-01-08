# File Upload Cloud (cloudinary)

File upload menggunakna layanan cloud.

## Install dependencies
```kts
implementation("com.cloudinary:cloudinary-http44:1.38.0")
```

## Set Cloudinary Properties
```yaml
cloudinary:
  cloud-name: cloud name
  api-key: cloudinary api key
  api-secret: cloudinary api secret
```
- melakukan set up env untuk cloudinary pada file `application.yml`

## Mengaktifkan Properties Scan
```kotlin
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@ConfigurationPropertiesScan
@SpringBootApplication
class NDaysBackendApplication

fun main(args: Array<String>) {
	runApplication<NDaysBackendApplication>(*args)
}
```
- `@ConfigurationPropertiesScan` untuk mengaktifkan properties scan agar spring dapat membaca env yang telah di set pada `application.yml`

## Cloudinary Config Bean
```kotlin
@Configuration
class CloudinaryConfig (
    private val cloudinaryProperties: CloudinaryProperties
) {

    @Bean
    fun cloudinary(): Cloudinary{
        val config = mapOf(
            "cloud_name" to cloudinaryProperties.cloudName,
            "api_key" to cloudinaryProperties.apiKey,
            "api_secret" to cloudinaryProperties.apiSecret
        )
        return Cloudinary(config)
    }
}
```
- Untuk mendaftarkan cloudinary ke spring

## Cloudinary Properties
```kotlin
@Configuration
@ConfigurationProperties(prefix = "cloudinary")
class CloudinaryProperties {
    lateinit var cloudName: String
    lateinit var apiKey: String
    lateinit var apiSecret: String
}
```
- Mengambil env pada `application.yml` lalu melakukan mapping ke field kotlin

## Cloudinary Service
```kotlin
@Service
class CloudinaryService (
    private val cloudinary: Cloudinary
) {

    fun uploadImage(
        file: MultipartFile,
        folder: String = "default"
    ): CloudinaryUploadResponse{
        val result = cloudinary.uploader().upload(
            file.bytes,
            mapOf(
                "folder" to folder,
                "resource_type" to "image"
            )
        )

        return CloudinaryUploadResponse(
            publicId = result["public_id"] as String,
            secureUrl = result["secure_url"] as String
        )
    }

    fun deleteImage(
        publicId: String
    ){
        cloudinary.uploader().destroy(
            publicId,
            mapOf(
                "resource_type" to "image"
            )
        )
    }
}
```
- Layer yang bertujuan untuk berinteraksi dengan cloudinary
- function `uploadImage`:
  - Menyimpan file ke cloudinary, mengembalikan data penting untuk disimpan ke database
  - Parameter:
    - file: file dari request body
    - folder: nama folder cloudinary (default = `default`)
  - Proses upload:
    ```kotlin
    val result = cloudinary.uploader().upload(
            file.bytes,
            mapOf(
                "folder" to folder,
                "resource_type" to "image"
            )
        )
    ```
    - File diambil dari request
    - Dikirim ke Cloudinary 
    - Disimpan sebagai image resource 
    - Masuk ke folder tertentu
    - Jika proses berhasil cloudinary akan mengembalikan response berupa `Map<String, Any>` isi response diantaranya:
      | Kategori            | Key                 | Fungsi / Keterangan |
      |---------------------|---------------------|---------------------|
      | Identitas File      | public_id           | ID unik image (dipakai untuk delete / update) |
      |                     | asset_id            | ID internal Cloudinary |
      |                     | version             | Versi upload (cache busting) |
      |                     | signature           | Signature Cloudinary |
      | URL Akses           | url                 | URL HTTP (tidak aman) |
      |                     | secure_url          | URL HTTPS (WAJIB dipakai) |
      | Lokasi & Tipe       | folder              | Folder tempat file disimpan |
      |                     | resource_type       | image / video / raw |
      |                     | type                | upload (default) |
      |                     | format              | jpg / png / webp |
      | Properti Image      | width               | Lebar image (px) |
      |                     | height              | Tinggi image (px) |
      |                     | bytes               | Ukuran file (dalam byte) |
      |                     | pages               | Jumlah halaman (PDF) |
      | Metadata            | created_at          | Waktu upload |
      |                     | etag                | Hash file |
      |                     | original_filename   | Nama file asli |
  - return:
    - public_id : Untuk delete / replace image
    - secure_url: Untuk ditampilkan ke frontend
- function `deleteImage`:
  - Menghapus file image di Cloudinary berdasarkan publicId

## Cloudinary DTO
```kotlin
class CloudinaryUploadResponse (
    var publicId: String,
    var secureUrl: String
)
```

