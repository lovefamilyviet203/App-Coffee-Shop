package com.example.coffeeshop.Helper

import android.content.Context
import com.example.coffeeshop.domain.UserModel

class ManagmentUser(val context: Context) {

    private val tinyDB = TinyDB(context)

    companion object {
        const val KEY_USERS = "UserList"
        const val KEY_LOGGED_IN = "isLoggedIn"
        const val KEY_CURRENT_USER = "currentUser"
    }

    fun register(name: String, email: String, phone: String, password: String): RegisterResult {
        val users = getUserList()

        if (users.any { it.email.equals(email, ignoreCase = true) }) {
            return RegisterResult.EMAIL_EXISTS
        }

        val user = UserModel(
            name = name,
            email = email,
            phone = phone,
            password = hashPassword(password)
        )
        users.add(user)
        tinyDB.putListGeneric(KEY_USERS, users)

        // Auto save profile info
        tinyDB.putString("profile_name", name)
        tinyDB.putString("profile_email", email)
        tinyDB.putString("profile_phone", phone)

        return RegisterResult.SUCCESS
    }

    fun login(email: String, password: String): Boolean {
        val users = getUserList()
        val hashed = hashPassword(password)
        val user = users.find {
            it.email.equals(email, ignoreCase = true) && it.password == hashed
        } ?: return false

        tinyDB.putBoolean(KEY_LOGGED_IN, true)
        tinyDB.putString(KEY_CURRENT_USER, user.email)

        // Sync profile
        tinyDB.putString("profile_name", user.name)
        tinyDB.putString("profile_email", user.email)
        tinyDB.putString("profile_phone", user.phone)

        return true
    }

    fun logout() {
        tinyDB.putBoolean(KEY_LOGGED_IN, false)
        tinyDB.putString(KEY_CURRENT_USER, "")
    }

    fun isLoggedIn(): Boolean = tinyDB.getBoolean(KEY_LOGGED_IN)

    fun getCurrentUserName(): String = tinyDB.getString("profile_name").ifEmpty { "Guest" }

    private fun getUserList(): ArrayList<UserModel> {
        return tinyDB.getListGeneric(KEY_USERS, UserModel::class.java)
    }

    // Simple hash to avoid storing plain text passwords
    private fun hashPassword(password: String): String {
        var hash = 0L
        for (ch in password) {
            hash = (hash * 31 + ch.code) and 0xFFFFFFFFL
        }
        return "h_${hash}_${password.length}"
    }

    enum class RegisterResult {
        SUCCESS, EMAIL_EXISTS
    }
}
