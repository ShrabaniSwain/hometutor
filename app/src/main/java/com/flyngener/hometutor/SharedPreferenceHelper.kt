package com.flyngener.hometutor

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceHelper(val context: Context) {

    companion object {
        private const val PREF_NAME = "LoginPreference"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_ID = "userId"
        private const val KEY_IS_GUARDIAN = "isGuardian"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun saveLoginData(userID: String, isGuardian: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER_ID, userID)
        editor.putString(KEY_IS_GUARDIAN, isGuardian)
        editor.apply()
    }

    fun getCustomerId(context: Context): String {
        return getSharedPreferences(context).getString(KEY_USER_ID, "") ?: ""
    }

    fun getCustomerName(context: Context): String {
        return getSharedPreferences(context).getString(KEY_IS_GUARDIAN, "") ?: ""
    }

    fun clearSession() {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, false)
        editor.putString(KEY_USER_ID, "")
        editor.putString(KEY_IS_GUARDIAN, "")
        editor.apply()
    }
}