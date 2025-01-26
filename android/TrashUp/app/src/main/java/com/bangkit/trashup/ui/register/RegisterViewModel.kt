package com.bangkit.trashup.ui.register

import UserRepository
import androidx.lifecycle.ViewModel

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun register(name: String, email: String, password: String
    ) = userRepository.register(name, email, password)
}