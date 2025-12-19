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




