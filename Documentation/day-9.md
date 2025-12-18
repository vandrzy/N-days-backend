# Role Based Access Control (RBAC)

Mekanisme hak akses yang menentukan apa saja yang dapat dilakukan oleh user berdasarkan perannya (role) dalam sebuah sistem

## Entity User
```kotlin
@Entity
@Table(name = "users")
class User (
    @Id
    val id: String,
    @Column(name = "username")
    var username: String,
    @Column(name = "password")
    var password: String,
    @Column(name = "role")
    @Enumerated(value = EnumType.STRING)
    val role: Role
)
```
## Enum Class Role
```kotlin
enum class Role (val canSelectedByUser: Boolean) {
    ROLE_USER(true),
    ROLE_ADMIN(false)
}
```
Berfungsi untuk mengintrol role yang dapat dipilih oleh 

## Custom User Detail
```kotlin
class CustomUserDetail(private val user: User): UserDetails {
    override fun getAuthorities() = listOf(SimpleGrantedAuthority(user.role.name))

    override fun getPassword() = user.password

    override fun getUsername() = user.username


}
```
Berfungsi untuk mengubah entity User agar dapat dikenali oleh Spring Security (Spring Security hanya mengenali UserDetails)
- `getAuthorities()` berfungsi untuk mendapatkan role dari user agar RBAC dapat dijalankan

## User Detail Service
```kotlin
class UserDetailService(
    private val usersRepository: UserRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String): CustomUserDetail {
        val user = usersRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("Username tidak ada")

        return CustomUserDetail(user)
    }
}
```
Berfungsi untuk mencari username dari database serta mengembalikannya dalam User Detail agar dapat dimengerti Spring Security

## JWT Service
```kotlin
@Service
class JwtService {

    private val key = "your-256-bit-secret-your-256-bit-secret"
    private val secretKey = Keys.hmacShaKeyFor(key.toByteArray())

    fun extractUsername(token: String): String{
        return extractClaims(token).subject
    }

    fun extractExpiration(token: String): Date{
        return extractClaims(token).expiration
    }

    fun generateToken(user: UserDetails): String{
        return Jwts.builder()
            .signWith(secretKey)
            .subject(user.username)
            .claim("role", user.authorities.first().authority)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + 3600000))
            .compact()
    }

    fun isTokenValid (user: UserDetails, token: String): Boolean{
        val username = extractUsername(token)
        return user.username == username && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean{
        return extractExpiration(token).before(Date())
    }

    private fun extractClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
}
```
Berfungsi untuk mengelola JWT
- Pada `generateToken` terdapat fungsi `.claim("role", user.authorities.first().authority)` berfungsi untuk menyimpan role user ke dalam token 

## Jwt Auth Filter
```kotlin
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
```
Berfungsi untuk mengecek token dan mengisi Security Context Holder

## Security Configuration 
```kotlin
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration (
    private val userDetailService: UserDetailService,
    private val jwtAuthFilter: JwtAuthFilter
) {
    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain{
        httpSecurity
            .csrf{it.disable()}
            .authorizeHttpRequests {
                it.requestMatchers("/api/auth/**").permitAll()
                    .anyRequest().authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter,UsernamePasswordAuthenticationFilter::class.java)

        return httpSecurity.build()
    }

    @Bean
    fun authenticationProvider():AuthenticationProvider{
        val provider = DaoAuthenticationProvider(userDetailService)
        provider.setPasswordEncoder(BCryptPasswordEncoder())

        return provider
    }


    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager



}
```
Jika ingin menerapkan RBAC terdapat annotation yang harus ditambahkan pada Security Configuration yaitu `@EnableMethodSecurity`

## Implementasi RBAC pada Controller
```kotlin
@RestController
class UserController (
    private val userService: UserService
){

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/users/me")
    fun getProfile(@AuthenticationPrincipal user:UserDetails): WebResponse<GetProfileResponse>{
        val response = userService.getProfile(user.username)

        return WebResponse(
            response
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/users")
    fun getAllProfile(): WebResponse<List<GetProfileResponse>>{
        val response = userService.getAllProfile()
        return WebResponse(
            response
        )
    }


}
```
RBAC di controller ini memastikan setiap endpoint hanya bisa diakses oleh user dengan role yang sesuai sebelum logika bisnis dijalankan.
- Penerapannya dapat dilihat pada annotation `@PreAuthorize("hasRole('ADMIN')")`
- Role yang tersimpan harus dalam format `ROLE_{NAMA ROLE}`




