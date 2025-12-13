package ndays.backend.main.configuration

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class TokenBlacklistService (
    private val jwtService: JwtService
){

    private val blacklist = ConcurrentHashMap<String, Long>()

    fun blacklist(token:String){
        val expiration = jwtService.extractExpiration(token).time
        blacklist[token] = expiration
    }

    fun isBlacklisted(token: String): Boolean {
        val exp: Long = blacklist[token] ?: return false

        if (System.currentTimeMillis() > exp) {
            blacklist.remove(token)
            return false
        }
        return true
    }
}