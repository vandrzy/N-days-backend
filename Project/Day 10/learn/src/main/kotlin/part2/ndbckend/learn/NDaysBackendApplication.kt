package part2.ndbckend.learn

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NDaysBackendApplication

fun main(args: Array<String>) {
	runApplication<NDaysBackendApplication>(*args)
}
