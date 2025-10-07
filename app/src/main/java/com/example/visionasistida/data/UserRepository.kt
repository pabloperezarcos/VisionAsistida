package com.example.visionasistida.data

class UserRepository(private val dao: UserDao) {
    val usersFlow = dao.observeAll()

    suspend fun register(email: String, password: String): AddResult {
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches())
            return AddResult.INVALID
        if (password.length < 6) return AddResult.INVALID
        if (dao.count() >= 5) return AddResult.LIMIT_REACHED
        if (dao.findByEmail(email.trim()) != null) return AddResult.DUPLICATE

        dao.insert(UserEntity(email = email.trim(), password = password))
        return AddResult.OK
    }

    suspend fun validateLogin(email: String, password: String): Boolean {
        val u = dao.findByEmail(email.trim()) ?: return false
        return u.password == password
    }

    suspend fun existsEmail(email: String): Boolean = dao.findByEmail(email.trim()) != null
    suspend fun deleteUser(id: Long) = dao.deleteById(id)

    enum class AddResult { OK, LIMIT_REACHED, DUPLICATE, INVALID }
}
