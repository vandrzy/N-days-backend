package part2.ndbckend.learn.service

import org.springframework.stereotype.Service
import part2.ndbckend.learn.entity.User
import part2.ndbckend.learn.model.user.GetProfileResponse
import part2.ndbckend.learn.repository.UserRepository

@Service
class UserService (
    private val userRepository: UserRepository
) {

    fun getProfile(username: String): GetProfileResponse{
        val user = userRepository.findByUsername(username)!!

        return toGetProfileResponse(user)
    }

    fun getAllProfile(): List<GetProfileResponse>{
        return userRepository.findAll().map {
            value -> toGetProfileResponse(value)
        }
    }

    private fun toGetProfileResponse(user: User): GetProfileResponse{
        return GetProfileResponse(
            username = user.username,
            role = user.role
        )
    }
}