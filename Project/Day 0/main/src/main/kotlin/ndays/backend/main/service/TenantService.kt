package ndays.backend.main.service

import ndays.backend.main.configuration.JwtService
import ndays.backend.main.entity.Tenant
import ndays.backend.main.model.LoginTenantResponse
import ndays.backend.main.model.RegisterTenantRequest
import ndays.backend.main.repository.TenantRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TenantService (
    @Autowired
    private val tenantRepository: TenantRepository,
    @Autowired
    private val authenticationManager: AuthenticationManager,
    @Autowired
    private val jwtService: JwtService
) {

    fun register (registerTenantRequest: RegisterTenantRequest){
        val tenant = Tenant(
            id = "tenant- " + UUID.randomUUID().toString(),
            username = registerTenantRequest.username,
            password = BCryptPasswordEncoder().encode(registerTenantRequest.password),
            role = "USER"
        )

        tenantRepository.save(tenant)
    }

    fun login(registerTenantRequest: RegisterTenantRequest): LoginTenantResponse{
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(registerTenantRequest.username, registerTenantRequest.password)
        )

        val user = tenantRepository.findByUsername(registerTenantRequest.username)
            ?: throw Exception("User Not Found")

        val userDetails = User
            .withUsername(user.username)
            .password(user.password)
            .roles(user.role)
            .build()

        val jwt = jwtService.generateToken(userDetails)

        return LoginTenantResponse(jwt)

    }
}