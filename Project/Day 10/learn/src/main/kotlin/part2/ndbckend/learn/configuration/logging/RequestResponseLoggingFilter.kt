package part2.ndbckend.learn.configuration.logging

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class RequestResponseLoggingFilter: OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val start = System.currentTimeMillis()

        try {
          filterChain.doFilter(request, response)
        } finally {
            val duration =System.currentTimeMillis() - start

            if (duration > 3000){
                log.warn(
                    "Slow request: method={} uri={} duration={}ms",
                    request.method,
                    request.requestURI,
                    duration
                )
            }

            if (response.status >= 500){
                log.warn(
                    "Server error: method={} uri={} duration={}ms",
                    request.method,
                    request.requestURI,
                    response.status
                )
            }
        }
    }
}