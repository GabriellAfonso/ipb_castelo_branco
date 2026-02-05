package com.gabrielafonso.ipb.castelobranco.ui.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabrielafonso.ipb.castelobranco.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

data class RegisterErrors(
    val username: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val password: String? = null,
    val passwordConfirm: String? = null,
    val general: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _registerErrors = MutableStateFlow(RegisterErrors())
    val registerErrors: StateFlow<RegisterErrors> = _registerErrors.asStateFlow()

    fun clearLoginError() {
        _loginError.value = null
    }

    fun clearRegisterErrors() {
        _registerErrors.value = RegisterErrors()
    }

    fun singIn(username: String, password: String) {
        viewModelScope.launch {
            _loginError.value = null
            try {
                val result = repository.signIn(username, password)
                result.onSuccess { authResponse ->
                    Log.d(TAG, "Login sucesso: $authResponse")
                }.onFailure { throwable ->
                    _loginError.value = throwable.message ?: "Erro ao fazer login"
                    Log.e(TAG, "Falha no login", throwable)
                }
            } catch (e: Exception) {
                _loginError.value = e.message ?: "Erro inesperado"
                Log.e(TAG, "Erro inesperado no login", e)
            }
        }
    }

    fun singUp(
        username: String,
        firstName: String,
        lastName: String,
        password: String,
        passwordConfirm: String
    ) {
        viewModelScope.launch {
            _registerErrors.value = RegisterErrors()
            try {
                val result = repository.signUp(username, firstName, lastName, password, passwordConfirm)
                result.onSuccess { authResponse ->
                    Log.d(TAG, "Registro sucesso: $authResponse")
                }.onFailure { throwable ->
                    val message = throwable.message ?: "Erro ao registrar"
                    _registerErrors.value = parseRegisterError(message)
                    Log.e(TAG, "Falha no registro", throwable)
                }
            } catch (e: Exception) {
                _registerErrors.value = RegisterErrors(general = e.message ?: "Erro inesperado")
                Log.e(TAG, "Erro inesperado no registro", e)
            }
        }
    }

    private fun parseRegisterError(message: String): RegisterErrors {
        return try {
            val trimmed = message.trim()
            // tenta interpretar como JSON objeto { field: [ "msg" ] } ou { "detail": ["..."] }
            if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
                return RegisterErrors(general = message)
            }

            val json = JSONObject(trimmed)
            var errors = RegisterErrors()

            val keys = json.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val value = json.get(key)
                val msg = when (value) {
                    is JSONArray -> if (value.length() > 0) value.getString(0) else value.toString()
                    else -> value.toString()
                }

                errors = when (key) {
                    "username" -> errors.copy(username = msg)
                    "first_name", "firstName" -> errors.copy(firstName = msg)
                    "last_name", "lastName" -> errors.copy(lastName = msg)
                    "password" -> errors.copy(password = msg)
                    "password_confirm", "passwordConfirm" -> errors.copy(passwordConfirm = msg)
                    "detail" -> errors.copy(general = msg)
                    else -> errors.copy(general = (errors.general?.let { "$it\n$msg" } ?: msg))
                }
            }

            if (errors == RegisterErrors()) RegisterErrors(general = message) else errors
        } catch (e: Exception) {
            RegisterErrors(general = message)
        }
    }

    fun signInWithGoogle() {
        // iniciar fluxo de login com Google
    }
}