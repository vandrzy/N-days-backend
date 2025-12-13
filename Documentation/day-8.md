# Json Web Token

## Dependencies

```kotlin
implementation("io.jsonwebtoken:jjwt-api:0.12.5")
runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")
```

## Membuat User Detail Service
```kotlin
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
```
`TenantDetailService` adalah implementasi dari `UserDetailsService` pada Spring Security yang berfungsi **menyediakan data user untuk proses autentikasi dan otorisasi**.

Service ini **khusus digunakan oleh Spring Security**, bukan untuk kebutuhan bisnis (CRUD tenant).

## Konfigurasi Security
```kotlin
@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val jwtAuthFilter: JwtAuthFilter,
    private val tenantDetailService: TenantDetailService
) {

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("api/secret/**").authenticated()
                    .anyRequest().permitAll()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return httpSecurity.build()
    }


    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(tenantDetailService)
        provider.setPasswordEncoder(BCryptPasswordEncoder())

        return provider
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager
}
```
`SecurityConfiguration` adalah kelas konfigurasi utama Spring Security yang mengatur **aturan keamanan aplikasi**, mulai dari autentikasi, otorisasi, hingga mekanisme JWT.

Kelas ini berperan sebagai **gerbang keamanan (security gate)** untuk seluruh request yang masuk ke aplikasi.
- Fungsi `securityFilterChain` mendefinisikan alur filter keamanan yang akan dijalankan untuk setiap request HTTP.
  - `.sessionManagement` Spring Security tidak menyimpan session, Setiap request harus membawa JWT sendiri
  - `.authenticationProvider(authenticationProvider())` Menentukan cara Spring Security memverifikasi user:
  - `.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)` JwtAuthFilter dijalankan sebelum proses login bawaan Spring, mengecek header **Authorization**, validasi jwt.
- Fungsi `authenticationProvider()` menghubungkan Spring Security dengan database user
- Fungsi `authenticationManager(config: AuthenticationConfiguration)` digunakan saat login untuk validasi username dan password.

## JWT Service
```kotlin
@Service
class JwtService {

    private val secret = "your-256-bit-secret-your-256-bit-secret"
    private val secretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    fun extractUsername(token:String): String {
        val claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)

        return claims.payload.subject
    }

    fun extractExpiration(token:String): Date{
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
            .expiration
    }

    fun generateToken(user: UserDetails): String{
        return Jwts.builder()
            .subject(user.username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + 3600000))
            .signWith(secretKey)
            .compact()
    }

    fun isTokenValid (token: String, user: UserDetails): Boolean{
        val username = extractUsername(token)
        return username == user.username && !isTokenExpired(token)
    }

    private fun isTokenExpired (token:String): Boolean{
        val expiration = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)

        return expiration.payload.expiration.before(Date())
    }
}
```
`JwtService` adalah service yang bertanggung jawab penuh untuk **mengelola JSON Web Token (JWT)** dalam aplikasi, mulai dari pembuatan token hingga validasi token pada setiap request.

Service ini menjadi **inti dari mekanisme autentikasi JWT**.
- Secret key
  ```kotlin
    private val secret = "your-256-bit-secret-your-256-bit-secret"
    private val secretKey = Keys.hmacShaKeyFor(secret.toByteArray())
    ```
  `secret key` adalah **kunci rahasia** yang digunakan untuk **menandatangani (sign)** dan **memverifikasi** JSON Web Token (JWT). 
- Fungsi `extractUsername()` digunakan untuk memperoleh username yang tersimpan pada payload token
- Fungsi `extractExpiration()` digunakan untuk memperoleh expiration date yang berupa milisecond yang tersimpan pada payload token
- Fungsi `generateToken()` digunakan untuk menggenerate token
- Fungsi `isTokenValid()` digunakan untuk validasi token
- Fungsi `isTOkenExpired()` digunakan untuk mengecek token expired


## JWT Auth Filter
```kotlin
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

            
            // Validasi token serta isi security context agar Spring Security mengenali user saat akses endpoint
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
```
`JwtAuthFilter` adalah **filter Spring Security khusus JWT** yang dijalankan **sebelum request mencapai controller**.  
Fungsi utamanya adalah **memeriksa validitas token JWT** dan **mengisi SecurityContext** agar Spring Security mengetahui siapa user yang sedang mengakses API.

## Auth Service
```kotlin
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
```
`TenantService` adalah **service layer** yang menangani logika bisnis terkait tenant, seperti:
- Registrasi tenant baru
- Login tenant (menghasilkan JWT)
    - Autentikasi username dengan password dengan Spring Security, spring security akan mengakses ke database
    - Ambil data user dari database
    - Membuat user detail yang akan digunakan untuk membuat jwt
    - Membuat JWT
    - Mengembalikan token JWT



## Auth Controller

```kotlin
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
```
Tenant Controller merupakan controller yang mengangani endpoint terkait tenant


