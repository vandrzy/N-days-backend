package ndays.backend.main.service

import ndays.backend.main.repository.TenantRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class TenantDetailService(
    private val tenantRepository: TenantRepository
): UserDetailsService { // Mengambil data user

    // kenapa harus dipecah dengan tenant service
    // detail service fokus untuk security
    // service fokus tenant management (update, delete, create)
    override fun loadUserByUsername(username: String): UserDetails {
        val tenant = tenantRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User Not Found")

        return User.withUsername(tenant.username)
            .password(tenant.password)
            .roles(tenant.role)
            .build()
    }

}