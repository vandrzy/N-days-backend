package part2.ndbckend.learn.auth

import org.apache.http.HttpStatus
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.web.server.ResponseStatusException
import part2.ndbckend.learn.configuration.jwt.JwtService
import part2.ndbckend.learn.entity.Role
import part2.ndbckend.learn.entity.User
import part2.ndbckend.learn.model.auth.AuthRequest
import part2.ndbckend.learn.repository.UserRepository
import part2.ndbckend.learn.service.AuthService
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class AuthServiceTest {
    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var authenticationManager: AuthenticationManager

    @Mock
    lateinit var jwtService: JwtService

    @InjectMocks
    lateinit var authService: AuthService

    @Test
    fun `registration success test`(){
        val request = AuthRequest(
            "vandy", "12345", Role.ROLE_USER
        )

        whenever(userRepository.findByUsername("vandy"))
            .thenReturn(null)

        whenever(userRepository.save(any()))
            .thenAnswer{it.arguments[0]}

        val response = authService.registration(request)

        assertEquals("Registration Success", response.message)

        verify(userRepository, times(1)).save(any())
    }

    @Test
    fun `user already exist`(){
        val request = AuthRequest(
            "vandy", "12345", Role.ROLE_USER
        )

        whenever(userRepository.findByUsername("vandy"))
            .thenReturn(User("a","vandy", "12345", Role.ROLE_USER))

        val exeption = assertThrows<ResponseStatusException> {
            authService.registration(request)
        }

        assertEquals(org.springframework.http.HttpStatus.CONFLICT, exeption.statusCode)

        assertEquals("Username already exists", exeption.reason)

        verify(userRepository, never()).save(any())

    }
}