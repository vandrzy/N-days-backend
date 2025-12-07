# Basic Spring Security

## Spring Security
Spring Security adalah framework keamanan untuk:

- Mengelola autentikasi
- Mengelola otorisasi
- Melindungi endpoint API
- Menangani session, CSRF, CORS, dll.

## Menambahkan dependencies
```kotlin
implementation("org.springframework.boot:spring-boot-starter-security") // spring security
implementation("org.springframework.security:spring-security-crypto") // Password encoder
```

## Melakukan Basic Configuration
```kotlin
@Configuration //Anotasi ini menandakan bahwa kelas ini berisi bean konfigurasi untuk Spring.
@EnableWebSecurity //Mengaktifkan fitur Spring Security pada aplikasi.
class SecurityConfiguration {

    @Bean // agar dibuat hanya 1 kali
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain{
        httpSecurity
            .csrf{it.disable()}
            .authorizeHttpRequests{ // membuat aturan authorization
                it.requestMatchers("/api/secret/**").authenticated()
                it.anyRequest().permitAll()

            }
            .httpBasic{} // membuat login melalui header
            .formLogin{} // mengaktifkan form login bawaan
        return httpSecurity.build() // membangun security dan mendaftarkannya ke build
    }
}
```
`SecurityFilterChain` Spring Boot versi terbaru menggunakan SecurityFilterChain sebagai cara utama untuk mengonfigurasi security.
Fungsi:
- Menentukan bagaimana request diproses oleh filter Spring Security.
- Mengatur aturan autentikasi, otorisasi, login, CSRF, dll.

CSRF (Cross Site Request Forgery) `csrf` adalah fitur keamanan untuk aplikasi berbasis form login. Untuk API (terutama REST API), CSRF biasanya dinonaktifkan, karena:
- Kebanyakan API menggunakan token/Basic Auth.
- API tidak menggunakan cookie-based session seperti aplikasi web.

## Membuat User Dummy
```kotlin
@Bean
    fun userDetailService(): UserDetailsService {
        val user = User.withUsername("van")
            .password(passwordEncoder().encode("12345"))
            .roles("USER")
            .build()


        return InMemoryUserDetailsManager(user)
    }
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
```
Membuat user yang akan disimpan sebagai memori sementara

## Endpoint Yang Dilindungi
```kotlin
@RestController
class GlobalController {    
    @GetMapping("/api/public/hello")
    fun publicApi(): String{
        return "Hello in public"
    }

    @GetMapping("/api/secret/hello")
    fun secretApi(): String{
        return "Hello in secret"
    }
}
```
Endpoint `/secret/**` memerlukan autentikasi terlebih dahulu. Untuk mengaksess endpoint tersebut diperlukan login dengan user dummy.

Selain endpoint `/secret/**` dapat diakses secara langsung tanpa memerlukan login dahulu

## Login Pada Postman
1. Buka Postman
2. Pada tab Authorization → pilih Basic Auth
3. Masukkan:
   - Username: van
   - Password: 12345