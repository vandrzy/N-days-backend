package part2.ndbckend.learn.configuration.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date

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