package ndays.backend.main.configuration

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date

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