package com.example.visionasistida.data

import android.util.Patterns
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.visionasistida.util.isValidEmail
import com.example.visionasistida.util.isStrongPassword

sealed interface Account { val email: String; val password: String }
data class BasicUser(override val email: String, override val password: String): Account
data class AdminUser(override val email: String, override val password: String, val canModerate: Boolean = true): Account

object UserStore {

    val users: SnapshotStateList<Account> = mutableStateListOf(
        BasicUser("ana@example.com", "Ana123"),
        BasicUser("benja@example.com", "Benja123"),
        BasicUser("caro@example.com", "Caro123"),
        AdminUser("dario@example.com", "Dario123", canModerate = true),
        BasicUser("eva@example.com", "Eva123")
    )

    enum class AddResult { OK, LIMIT_REACHED, DUPLICATE, INVALID }

    fun addUser(email: String, password: String): AddResult {
        return try {
            if (!email.isValidEmail() || !password.isStrongPassword()) return AddResult.INVALID
            if (users.size >= 5) return AddResult.LIMIT_REACHED

            for (u in users) {
                if (u.email.equals(email.trim(), ignoreCase = true)) return AddResult.DUPLICATE
            }
            users.add(BasicUser(email.trim(), password))
            AddResult.OK
        } catch (_: Exception) {
            AddResult.INVALID
        }
    }

    fun validateLogin(email: String, password: String): Boolean {
        for (u in users) {
            if (u.email.equals(email.trim(), true) && u.password == password) return true
        }
        return false
    }

    fun clearAll() = users.clear()

    fun removeByEmail(email: String): Boolean {
        val idx = users.indexOfFirst { it.email.equals(email.trim(), true) }
        return if (idx >= 0) { users.removeAt(idx); true } else false
    }
}
