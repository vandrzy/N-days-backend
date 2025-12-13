package ndays.backend.main.configuration

import ndays.backend.main.service.TenantDetailService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val jwtAuthFilter: JwtAuthFilter,
    private val tenantDetailService: TenantDetailService
) {

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity):SecurityFilterChain{
        httpSecurity
            .csrf{it.disable()}
            .authorizeHttpRequests{
                it.requestMatchers("api/secret/**").authenticated()
                    .anyRequest().permitAll()
            }
            .sessionManagement{
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter,UsernamePasswordAuthenticationFilter::class.java)

        return httpSecurity.build()
    }


    @Bean
    fun authenticationProvider(): AuthenticationProvider{
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(tenantDetailService)
        provider.setPasswordEncoder(BCryptPasswordEncoder())

        return provider
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager

//    @Bean
//    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain{
//        httpSecurity
//            .csrf{it.disable()}
//            .authorizeHttpRequests{
//                it.requestMatchers("/api/secret/**").authenticated()
//                it.anyRequest().permitAll()
//
//            }
//            .httpBasic{}
//            .formLogin{}
//        return httpSecurity.build()
//    }

//    @Bean
//    fun userDetailService(): UserDetailsService {
//        val user = User.withUsername("van")
//            .password(passwordEncoder().encode("12345"))
//            .roles("USER")
//            .build()
//
//
//        return InMemoryUserDetailsManager(user)
//    }
//    @Bean
//    fun passwordEncoder() = BCryptPasswordEncoder()


}