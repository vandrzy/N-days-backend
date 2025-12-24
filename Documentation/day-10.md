# Global Error Handler

Mekanisme terpusat untuk menangani semua error/ exception yang terjadi. Pada Rest API biasanya diimplementasikan di
`@ControllerAdvice` atau `@RestControllerAdvice`

## API Error Response
```kotlin
class ApiErrorResponse (
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String?,
    val path: String
)
```
Response yang akan ditampilkan ketika ada exception yang terjadi

## Resource Not Found Exception
```kotlin
class ResourceNotFoundException (message: String): RuntimeException(message)
```
berfungsi untuk membuat custom exception yang digunakan ketika data/resource yang diminta tidak ditemukan (HTTP 404).

## Global Exception Handler
```kotlin
@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationError(ex: MethodArgumentNotValidException, request: HttpServletRequest):
    ResponseEntity<ApiErrorResponse>{
        val message = ex.bindingResult
            .fieldErrors
            .joinToString(", "){"${it.field}: ${it.defaultMessage}"}

        return ResponseEntity(
            ApiErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = HttpStatus.BAD_REQUEST.reasonPhrase,
                message = message,
                path = request.requestURI
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(
        ex: ResourceNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity(
            ApiErrorResponse(
                status = HttpStatus.NOT_FOUND.value(),
                error = HttpStatus.NOT_FOUND.reasonPhrase,
                message = ex.message,
                path = request.requestURI
            ),
            HttpStatus.NOT_FOUND
        )
    }


    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException, request: HttpServletRequest):
            ResponseEntity<ApiErrorResponse>{
        return ResponseEntity(
            ApiErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Validation Error",
                message = ex.message,
                path = request.requestURI
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(JwtException::class)
    fun handleJwtException(ex: JwtException, request: HttpServletRequest):
            ResponseEntity<ApiErrorResponse>{
        return ResponseEntity(
            ApiErrorResponse(
                status = HttpStatus.UNAUTHORIZED.value(),
                error = HttpStatus.UNAUTHORIZED.reasonPhrase,
                message = ex.message,
                path = request.requestURI
            ),
            HttpStatus.UNAUTHORIZED
        )
    }

    @ExceptionHandler(ExpiredJwtException::class)
    fun handleJwtException(ex: ExpiredJwtException, request: HttpServletRequest):
            ResponseEntity<ApiErrorResponse>{
        return ResponseEntity(
            ApiErrorResponse(
                status = HttpStatus.UNAUTHORIZED.value(),
                error = HttpStatus.UNAUTHORIZED.reasonPhrase,
                message = "Token expired",
                path = request.requestURI
            ),
            HttpStatus.UNAUTHORIZED
        )
    }
}
```
Berfungsi sebagai pusat penganganan error.
- `@RestControllerAdvice` berfungsi sebagai penanganan global untuk controller REST
- `@ExceptionHandler` Berfungsi untuk menangani exeption tertentu di spring

| Nama Exception                         | Sumber Terjadi                                   | HTTP Status | Alasan Perlu Ditangani |
|---------------------------------------|-------------------------------------------------|-------------|------------------------|
| MethodArgumentNotValidException       | Validasi @Valid pada @RequestBody DTO           | 400 Bad Request | Memberikan detail field input yang tidak valid |
| ConstraintViolationException          | Validasi @RequestParam / @PathVariable          | 400 Bad Request | Validasi parameter URL gagal |
| HttpMessageNotReadableException       | JSON request tidak valid / salah format         | 400 Bad Request | Mencegah error parsing JSON dikirim sebagai 500 |
| IllegalArgumentException              | Validasi manual di service                      | 400 Bad Request | Menandai kesalahan input dari client |
| ResourceNotFoundException (custom)    | Data tidak ditemukan di database                | 404 Not Found | Menyatakan resource memang tidak ada |
| EntityNotFoundException               | JPA getReferenceById                            | 404 Not Found | JPA gagal menemukan entity |
| DataIntegrityViolationException       | Pelanggaran constraint database (unique, FK)   | 409 Conflict | Menjaga konsistensi data |
| AuthenticationException               | Login gagal / token invalid                    | 401 Unauthorized | Client belum terautentikasi |
| ExpiredJwtException / JwtException    | JWT expired atau tidak valid                    | 401 Unauthorized | Token tidak bisa dipakai |
| AccessDeniedException                 | Role tidak sesuai / @PreAuthorize gagal         | 403 Forbidden | User login tapi tidak punya hak akses |
| HttpRequestMethodNotSupportedException| HTTP method tidak didukung endpoint             | 405 Method Not Allowed | Kesalahan penggunaan endpoint |
| NoHandlerFoundException               | Endpoint tidak ditemukan                        | 404 Not Found | Route API tidak tersedia |
| Exception (generic fallback)          | Error tidak terduga                             | 500 Internal Server Error | Mencegah kebocoran stack trace |




