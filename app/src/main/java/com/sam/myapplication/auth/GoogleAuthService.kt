package com.sam.myapplication.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.sam.myapplication.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GoogleAuthService(private val context: Context) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val credentialManager = CredentialManager.create(context)

    suspend fun signIn(webClientId: String): Result<User> {
        if (webClientId == "YOUR_WEB_CLIENT_ID_HERE") {
            return Result.failure(Exception("Please configure your Google Web Client ID in AttendanceApp.kt"))
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(context, request)
            handleSignIn(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun handleSignIn(result: GetCredentialResponse): Result<User> {
        val credential = result.credential
        if (credential is GoogleIdTokenCredential) {
            val user = User(
                id = credential.id,
                name = credential.displayName,
                email = credential.id, // Usually the email is in id for GoogleIdTokenCredential
                photoUrl = credential.profilePictureUri?.toString()
            )
            _currentUser.value = user
            return Result.success(user)
        }
        return Result.failure(Exception("Invalid credential type"))
    }

    suspend fun signOut() {
        credentialManager.clearCredentialState(androidx.credentials.ClearCredentialStateRequest())
        _currentUser.value = null
    }

    fun setCustomUser(username: String) {
        _currentUser.value = User(
            id = username,
            name = username.capitalize(),
            email = "$username@local.com",
            photoUrl = null
        )
    }
}
