package com.example.visionasistida.data

class UserRepository(private val dao: UserDao) {
    val usersFlow = dao.observeAll()

    suspend fun register(email: String, password: String): AddResult {
        val trimmed = email.trim()
        if (trimmed.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmed).matches())
            return AddResult.INVALID
        if (password.length < 6) return AddResult.INVALID
        if (dao.count() >= 5) return AddResult.LIMIT_REACHED
        if (dao.findByEmail(trimmed) != null) return AddResult.DUPLICATE

        val display = trimmed.substringBefore('@').replaceFirstChar { it.uppercase() }

        dao.insert(
            UserEntity(
                email = trimmed,
                password = password,
                isAdmin = false,
                displayName = display
            )
        )
        return AddResult.OK
    }

    suspend fun validateLogin(email: String, password: String): Boolean =
        dao.findByEmailAndPassword(email.trim(), password) != null

    suspend fun existsEmail(email: String): Boolean =
        dao.findByEmail(email.trim()) != null

    suspend fun deleteUser(id: Long) = dao.deleteById(id)

    // ← NUEVO
    suspend fun getDisplayName(email: String): String? =
        dao.getDisplayName(email.trim())

    enum class AddResult { OK, LIMIT_REACHED, DUPLICATE, INVALID }
}
