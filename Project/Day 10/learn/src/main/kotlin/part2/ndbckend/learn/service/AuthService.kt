package part2.ndbckend.learn.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import part2.ndbckend.learn.configuration.jwt.JwtService
import part2.ndbckend.learn.entity.User
import part2.ndbckend.learn.model.auth.AuthRequest
import part2.ndbckend.learn.model.auth.AuthResponse
import part2.ndbckend.learn.model.auth.LoginRequest
import part2.ndbckend.learn.model.auth.LoginResponse
import part2.ndbckend.learn.repository.UserRepository
import java.util.UUID
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Private


@Service
class AuthService (
    @Autowired
    private val userRepository: UserRepository,
    @Autowired
    private val authenticationManager: AuthenticationManager,
    @Autowired
    private val jwtService: JwtService
){

    fun registration(request: AuthRequest): AuthResponse{
        if (userRepository.findByUsername(request.username) != null){
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Username already exists"
            )
        }

        if (!request.role.canSelectedByUser){
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Role tidak ada"
            )
        }

        val user = User(
            id = "user-" + UUID.randomUUID().toString(),
            username = request.username,
            password = BCryptPasswordEncoder().encode(request.password)!!,
            role = request.role
        )



        userRepository.save(user)

        return AuthResponse(
            message = "Registration Success"
        )
    }

    fun login(request: LoginRequest):LoginResponse{
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )

        val user = userRepository.findByUsername(request.username) ?: throw ResponseStatusException(
            HttpStatus.CONFLICT,
            "Username not found"
        )
        val userDetail = org.springframework.security.core.userdetails.User.withUsername(user.username)
            .password(user.password)
            .authorities(user.role.name)
            .build()
        val jwt = jwtService.generateToken(userDetail)

        return LoginResponse(
            jwt
        )


    }
}