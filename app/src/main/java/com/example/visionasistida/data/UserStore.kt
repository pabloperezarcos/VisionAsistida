package com.example.visionasistida.data

import android.util.Patterns
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class User(val email: String, val password: String)

object UserStore {


    val users: SnapshotStateList<User> = mutableStateListOf()


    enum class AddResult { OK, LIMIT_REACHED, DUPLICATE, INVALID }

    private fun isValidEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    fun addUser(email: String, password: String): AddResult {
        val e = email.trim()
        if (!isValidEmail(e) || password.isBlank()) return AddResult.INVALID
        if (users.size >= 5) return AddResult.LIMIT_REACHED
        if (users.any { it.email.equals(e, ignoreCase = true) }) return AddResult.DUPLICATE

        users.add(User(e, password))
        return AddResult.OK
    }

    fun validateLogin(email: String, password: String): Boolean {
        val e = email.trim()
        return users.any { it.email.equals(e, true) && it.password == password }
    }

    fun clearAll() = users.clear()

    fun removeByEmail(email: String): Boolean {
        val idx = users.indexOfFirst { it.email.equals(email.trim(), true) }
        return if (idx >= 0) { users.removeAt(idx); true } else false
    }
}
