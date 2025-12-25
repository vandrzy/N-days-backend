package part2.ndbckend.learn.configuration.annotation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UniqueUsernameValidator::class])
annotation class UniqueUsername(
    val message: String = "Username sudah digunakan",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
