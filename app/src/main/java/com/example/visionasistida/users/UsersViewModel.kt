package com.example.visionasistida.users

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.visionasistida.data.AppDatabase
import com.example.visionasistida.data.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UsersViewModel(app: Application) : AndroidViewModel(app) {
    private val repo by lazy { UserRepository(AppDatabase.get(app).userDao()) }

    val users = repo.usersFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, emptyList()
    )

    fun deleteUser(id: Long) = viewModelScope.launch { repo.deleteUser(id) }
}
