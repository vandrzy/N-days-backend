package ndays.backend.main.repository

import ndays.backend.main.entity.Tenant
import org.springframework.data.jpa.repository.JpaRepository

interface TenantRepository: JpaRepository<Tenant, String> {

    fun findByUsername(username:String): Tenant?
}