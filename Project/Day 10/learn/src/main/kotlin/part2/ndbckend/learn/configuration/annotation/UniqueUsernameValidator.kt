package part2.ndbckend.learn.configuration.annotation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component
import part2.ndbckend.learn.repository.UserRepository


@Component
class UniqueUsernameValidator (
    private val userRepository: UserRepository
): ConstraintValidator<UniqueUsername, String> {
    override fun isValid(p0: String?, p1: ConstraintValidatorContext?): Boolean {

        if (p0.isNullOrBlank()) return true

        return !userRepository.existsByUsername(p0)
    }
}