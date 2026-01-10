package com.example.wordgame.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthUiState(
    val user: com.google.firebase.auth.FirebaseUser? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val authProvider: () -> FirebaseAuth = { FirebaseAuth.getInstance() }
) : ViewModel() {

    private val auth: FirebaseAuth? = runCatching { authProvider() }.getOrNull()
    private val _uiState = MutableStateFlow(
        AuthUiState(
            user = auth?.currentUser,
            error = if (auth == null) {
                "Firebase is not configured. Add google-services.json and apply the plugin."
            } else {
                null
            }
        )
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _uiState.update { it.copy(user = firebaseAuth.currentUser, isLoading = false) }
    }

    init {
        auth?.addAuthStateListener(authListener)
    }

    override fun onCleared() {
        auth?.removeAuthStateListener(authListener)
        super.onCleared()
    }

    fun signInWithGoogle(idToken: String) {
        if (idToken.isBlank()) {
            _uiState.update { it.copy(error = "Google sign-in failed. Try again.") }
            return
        }
        if (auth == null) {
            _uiState.update { it.copy(error = "Firebase is not configured. Unable to sign in.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = runCatching { auth.signInWithCredential(credential).await() }.getOrNull()
            if (result?.user == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Google sign-in failed. Please try again."
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = null) }
            }
        }
    }

    fun setError(message: String) {
        _uiState.update { it.copy(error = message) }
    }

    fun signOut() {
        auth?.signOut()
        _uiState.update {
            it.copy(
                user = null
            )
        }
    }

}
