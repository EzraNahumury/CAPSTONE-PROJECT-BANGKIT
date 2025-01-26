package com.bangkit.trashup.ui.login

import UserRepository
import androidx.lifecycle.ViewModel

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    fun login(email: String, password: String) = userRepository.login(email, password)
}