package com.example.visionasistida.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.visionasistida.data.AppDatabase
import com.example.visionasistida.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val repo by lazy { UserRepository(AppDatabase.get(app).userDao()) }

    suspend fun login(email: String, password: String): Boolean =
        withContext(Dispatchers.IO) { repo.validateLogin(email, password) }

    suspend fun register(email: String, password: String): UserRepository.AddResult =
        withContext(Dispatchers.IO) { repo.register(email, password) }

    suspend fun emailExists(email: String): Boolean =
        withContext(Dispatchers.IO) { repo.existsEmail(email) }

    suspend fun getDisplayName(email: String): String? =
        withContext(Dispatchers.IO) { repo.getDisplayName(email) }
}
