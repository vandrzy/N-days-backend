package part2.ndbckend.learn.entity

enum class Role (val canSelectedByUser: Boolean) {
    ROLE_USER(true),
    ROLE_ADMIN(false)
}