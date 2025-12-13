package ndays.backend.main.configuration

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import ndays.backend.main.service.TenantDetailService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.lang.RuntimeException

//untuk melakukan validasi pada request header
@Component
class JwtAuthFilter (
    private val jwtService: JwtService,
    private val tenantDetailService: TenantDetailService,
    private val tokenBlacklistService: TokenBlacklistService
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(7)
        val username = jwtService.extractUsername(token)

        try {


            if (tokenBlacklistService.isBlacklisted(token)) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("Sudah logout")
                return
            }

            if (username != null && SecurityContextHolder.getContext().authentication == null) {
                val userDetail = tenantDetailService.loadUserByUsername(username)

                if (jwtService.isTokenValid(token, userDetail)) {
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetail, null, userDetail.authorities
                    )

                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
        }
        catch (e: ExpiredJwtException){
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Token invalid")
            return
        }
        catch (e: JwtException){
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Token invalid")
            return
        }
        catch (e: RuntimeException){
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Token invalid")
            return
        }

        filterChain.doFilter(request, response)
    }

}