package com.sam.myapplication.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class CredentialManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCredentials(employeeNo: String, password: String) {
        sharedPreferences.edit()
            .putString("employee_no", employeeNo)
            .putString("password", password)
            .putBoolean("has_credentials", true)
            .apply()
    }

    fun getCredentials(): Pair<String?, String?> {
        val employeeNo = sharedPreferences.getString("employee_no", null)
        val password = sharedPreferences.getString("password", null)
        return Pair(employeeNo, password)
    }

    fun clearCredentials() {
        sharedPreferences.edit()
            .remove("employee_no")
            .remove("password")
            .putBoolean("has_credentials", false)
            .apply()
    }

    fun hasCredentials(): Boolean {
        return sharedPreferences.getBoolean("has_credentials", false)
    }

    var isBiometricEnabled: Boolean
        get() = sharedPreferences.getBoolean("biometric_enabled", false)
        set(value) = sharedPreferences.edit().putBoolean("biometric_enabled", value).apply()

    var rememberMe: Boolean
        get() = sharedPreferences.getBoolean("remember_me", false)
        set(value) = sharedPreferences.edit().putBoolean("remember_me", value).apply()
}
