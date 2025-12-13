package ndays.backend.main.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import ndays.backend.main.configuration.TokenBlacklistService
import ndays.backend.main.model.LoginTenantResponse
import ndays.backend.main.model.RegisterTenantRequest
import ndays.backend.main.model.WebResponse
import ndays.backend.main.service.TenantService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TenantController (
    @Autowired
    private val tenantService: TenantService,

    @Autowired
    private val tokenBlacklistService: TokenBlacklistService
){

    @PostMapping("/api/tenant/register")
    fun registerTenant(@RequestBody @Valid request: RegisterTenantRequest): WebResponse<String>{
        tenantService.register(request)

        return WebResponse(
            data = "Success",
            error = "Null"
        )
    }

    @PostMapping("/api/tenant/login")
    fun login(@RequestBody @Valid request: RegisterTenantRequest): WebResponse<LoginTenantResponse>{
        val response = tenantService.login(request)

        return WebResponse(
            data = response,
            error = "null"
        )
    }

    @PostMapping("/api/tenant/logout")
    fun logout(request: HttpServletRequest): WebResponse<String>{
        val authHeader = request.getHeader("Authorization")

        if (!authHeader.startsWith("Bearer ")){
            return WebResponse(
                data = "Invalid Authorization Header",
                error = "Invalid Authorization Header"
            )
        }
        val token = authHeader.substring(7)

        tokenBlacklistService.blacklist(token)

        return WebResponse(
            data = "Berhasil logout",
            error = null
        )
    }
}