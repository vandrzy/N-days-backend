package part2.ndbckend.learn.exeption

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

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