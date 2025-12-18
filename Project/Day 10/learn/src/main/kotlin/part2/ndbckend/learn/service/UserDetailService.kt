package part2.ndbckend.learn.service

import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import part2.ndbckend.learn.repository.UserRepository

@Service
class UserDetailService(
    private val usersRepository: UserRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String): CustomUserDetail {
        val user = usersRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("Username tidak ada")

        return CustomUserDetail(user)
    }
}