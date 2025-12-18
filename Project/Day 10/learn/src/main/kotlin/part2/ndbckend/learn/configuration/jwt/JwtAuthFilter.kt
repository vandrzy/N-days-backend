package part2.ndbckend.learn.configuration.jwt

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import part2.ndbckend.learn.service.UserDetailService

@Configuration
class JwtAuthFilter(
    val jwtService: JwtService,
    val userDetailService: UserDetailService
) : OncePerRequestFilter(){
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
            if (SecurityContextHolder.getContext().authentication == null){
                val userDetail = userDetailService.loadUserByUsername(username)

                if (jwtService.isTokenValid(userDetail, token)){
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetail, null, userDetail.authorities
                    )

                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

                    SecurityContextHolder.getContext().authentication = authToken

                }

            }

        }
        catch (e: JwtException){
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Token Invalid")
            return
        }
        catch (e: ExpiredJwtException){
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Token Invalid")
            return
        }

        filterChain.doFilter(request, response)
    }

}