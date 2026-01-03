package part2.ndbckend.learn

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@SpringBootApplication
class NDaysBackendApplication

fun main(args: Array<String>) {
	runApplication<NDaysBackendApplication>(*args)
}
