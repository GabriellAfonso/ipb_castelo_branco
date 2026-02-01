package com.gabrielafonso.ipb.castelobranco.ui.screens.auth


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabrielafonso.ipb.castelobranco.domain.repository.AuthRepository
import com.gabrielafonso.ipb.castelobranco.domain.repository.HymnalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }


    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val result = repository.signIn(username, password)
                result.onSuccess { authResponse ->
                    Log.d(AuthViewModel.Companion.TAG, "Login sucesso: $authResponse")
                    // atualizar estados/flows aqui conforme necessário
                }.onFailure { throwable ->
                    Log.e(AuthViewModel.Companion.TAG, "Falha no login", throwable)
                    // tratar erro (ex.: atualizar estado de erro)
                }
            } catch (e: Exception) {
                Log.e(AuthViewModel.Companion.TAG, "Erro inesperado no login", e)
            }
        }
    }

    fun signInWithGoogle() {
        // iniciar fluxo de login com Google
    }



}
