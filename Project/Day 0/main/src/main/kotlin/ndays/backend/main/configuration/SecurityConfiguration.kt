package ndays.backend.main.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain{
        httpSecurity
            .csrf{it.disable()}
            .authorizeHttpRequests{
                it.requestMatchers("/api/secret/**").authenticated()
                it.anyRequest().permitAll()

            }
            .httpBasic{}
            .formLogin{}
        return httpSecurity.build()
    }

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


}