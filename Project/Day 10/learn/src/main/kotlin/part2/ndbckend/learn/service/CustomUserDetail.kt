package part2.ndbckend.learn.service

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import part2.ndbckend.learn.entity.User

class CustomUserDetail(private val user: User): UserDetails {
    override fun getAuthorities() = listOf(SimpleGrantedAuthority(user.role.name))

    override fun getPassword() = user.password

    override fun getUsername() = user.username


}