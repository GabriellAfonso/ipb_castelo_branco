package com.gabrielafonso.ipb.castelobranco.domain.repository

import com.gabrielafonso.ipb.castelobranco.domain.model.AuthResponse

interface AuthRepository {
    suspend fun signIn(username: String, password: String): Result<AuthResponse>
    fun getAuthToken(): String?
    suspend fun signOut()
    suspend fun signUp(
        username: String,
        firstName: String,
        lastName: String,
        password: String,
        passwordConfirm: String
    ): Result<AuthResponse>
}
