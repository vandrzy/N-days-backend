# Custom Auth Filter

Filter pada security filter chain untuk menangani proses autentikasi secara custom. 

## Implementasi
```kotlin
@Component
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
            throw JwtException("There is a problem in your token")
        }
        catch (e: ExpiredJwtException){
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Token Invalid")
            return
        }

        filterChain.doFilter(request, response)
    }

}
```

- Implementasi `OncePerRequestFilter` custom auth filter hanya dijalankan sekali per request
- Method `doFilterInternal` Menjadi gerbang autentikasi JWT