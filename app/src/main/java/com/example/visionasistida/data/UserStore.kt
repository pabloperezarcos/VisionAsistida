package com.example.visionasistida.data

import android.util.Patterns
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

/** Modelo simple para la demo */
data class User(val email: String, val password: String)

/**
 * Almacén en memoria para cumplir la pauta:
 * - Máximo 5 usuarios
 * - Alta desde Registro
 * - Validación en Login
 */
object UserStore {

    /** Lista observable por Compose (para que Home se actualice sola) */
    val users: SnapshotStateList<User> = mutableStateListOf()

    /** Resultado de alta de usuario (útil para mensajes de UI) */
    enum class AddResult { OK, LIMIT_REACHED, DUPLICATE, INVALID }

    private fun isValidEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    /** Intenta añadir un usuario cumpliendo reglas de la actividad */
    fun addUser(email: String, password: String): AddResult {
        val e = email.trim()
        if (!isValidEmail(e) || password.isBlank()) return AddResult.INVALID
        if (users.size >= 5) return AddResult.LIMIT_REACHED
        if (users.any { it.email.equals(e, ignoreCase = true) }) return AddResult.DUPLICATE

        users.add(User(e, password))
        return AddResult.OK
    }

    /** Valida credenciales en Login */
    fun validateLogin(email: String, password: String): Boolean {
        val e = email.trim()
        return users.any { it.email.equals(e, true) && it.password == password }
    }

    /** Utilidades opcionales (por si quieres limpiar o borrar) */
    fun clearAll() = users.clear()

    fun removeByEmail(email: String): Boolean {
        val idx = users.indexOfFirst { it.email.equals(email.trim(), true) }
        return if (idx >= 0) { users.removeAt(idx); true } else false
    }
}
