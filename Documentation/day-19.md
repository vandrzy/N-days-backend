# Logging

Logging & Monitoring adalah dua pilar penting dalam pengelolaan aplikasi backend (termasuk Spring Boot / microservices) untuk mendeteksi error, menganalisis performa, dan menjaga stabilitas sistem.

## Konfigurasi Logback
```xml
<configuration>
    <property name="LOG_PATTERN"
    value="%d{yyyy-MM-dd HH:mm:ss} %-5level [%thread] %logger{36} - %msg%n"/>

    <appender name="CONSOLE"
    class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>

    <root level="WARN">
        <appender-ref ref="CONSOLE"/>
    </root>

    <logger name="part2.ndbckend.learn.exeption"/>

</configuration>
```
- `<configuration>` adalah root element Logback.
- Property `LOG_PATTERN` Mendefinisikan format log agar: Konsisten, Mudah dibaca, Mudah dicari di production
- Appender `CONSOLE` Menentukan ke mana log dikirim
- `<root level="WARN">` Menentukan level minimum global

## Logging Request/Response dengan OncePerRequestFilter
```kotlin
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
```
- Mendeteksi request yang lambat
- Mendeteksi response yang error (5xx)
- Tanpa mengganggu alur aplikasi



