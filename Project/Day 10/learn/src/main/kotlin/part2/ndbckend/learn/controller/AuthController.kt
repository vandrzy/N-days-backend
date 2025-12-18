package part2.ndbckend.learn.controller

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import part2.ndbckend.learn.model.WebResponse
import part2.ndbckend.learn.model.auth.AuthRequest
import part2.ndbckend.learn.model.auth.AuthResponse
import part2.ndbckend.learn.model.auth.LoginRequest
import part2.ndbckend.learn.model.auth.LoginResponse
import part2.ndbckend.learn.service.AuthService

@RestController
class AuthController (
    private val authService: AuthService
) {

    @PostMapping("/api/auth/registration")
    fun registration (@Valid @RequestBody authRequest: AuthRequest): WebResponse<AuthResponse>{
        val response = authService.registration(authRequest)
        return WebResponse(
            data = response
        )
    }

    @PostMapping("/api/auth/login")
    fun login (@Valid @RequestBody loginRequest: LoginRequest): WebResponse<LoginResponse>{
        val response = authService.login(loginRequest)
        return WebResponse(
            data = response
        )
    }
}