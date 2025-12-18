package part2.ndbckend.learn.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import part2.ndbckend.learn.model.WebResponse
import part2.ndbckend.learn.model.user.GetProfileResponse
import part2.ndbckend.learn.service.UserService

@RestController
class UserController (
    private val userService: UserService
){

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/users/me")
    fun getProfile(@AuthenticationPrincipal user:UserDetails): WebResponse<GetProfileResponse>{
        val response = userService.getProfile(user.username)

        return WebResponse(
            response
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/users")
    fun getAllProfile(): WebResponse<List<GetProfileResponse>>{
        val response = userService.getAllProfile()
        return WebResponse(
            response
        )
    }


}