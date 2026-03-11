package com.example.coffeeshop.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import com.example.coffeeshop.Helper.ManagmentUser
import com.example.coffeeshop.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.coffeeshop.Helper.TinyDB

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var tinyDB: TinyDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tinyDB = TinyDB(this)

        binding.backBtn.setOnClickListener { finish() }

        binding.goToLoginTxt.setOnClickListener { finish() }

        binding.registerBtn.setOnClickListener {
            attemptRegister()
        }
    }

    private fun attemptRegister() {
        hideError()

        val name = binding.nameInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val phone = binding.phoneInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()
        val confirmPassword = binding.confirmPasswordInput.text.toString()

        // Validate
        if (name.isEmpty()) {
            binding.nameInput.error = "Please enter your name"
            return
        }
        if (name.length < 2) {
            binding.nameInput.error = "Name must be at least 2 characters"
            return
        }
        if (email.isEmpty()) {
            binding.emailInput.error = "Please enter your email"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInput.error = "Enter a valid email address"
            return
        }
        if (phone.isEmpty()) {
            binding.phoneInput.error = "Please enter your phone number"
            return
        }
        if (password.isEmpty()) {
            binding.passwordInput.error = "Please create a password"
            return
        }
        if (password.length < 6) {
            binding.passwordInput.error = "Password must be at least 6 characters"
            return
        }
        if (confirmPassword != password) {
            binding.confirmPasswordInput.error = "Passwords do not match"
            showError("Passwords do not match")
            return
        }

        showLoading(true)

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: run {
                    binding.registerBtn.isEnabled = true
                    showError("Registration failed. Please try again.")
                    return@addOnSuccessListener
                }

                // ✅ Lưu vào Realtime Database (nhất quán với Orders)
                val userMap = hashMapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to email,
                    "phone" to phone,
                    "createdAt" to System.currentTimeMillis()
                )

                FirebaseDatabase.getInstance().reference
                    .child("Users")
                    .child(uid)
                    .setValue(userMap)
                    .addOnSuccessListener {
                        tinyDB.putString("profile_name", name)
                        tinyDB.putString("profile_email", email)
                        tinyDB.putString("profile_phone", phone)
                        goToMain()
                    }
                    .addOnFailureListener {
                        // DB lỗi nhưng Auth OK → vẫn cho vào app
                        tinyDB.putString("profile_name", name)
                        tinyDB.putString("profile_email", email)
                        tinyDB.putString("profile_phone", phone)
                        goToMain()
                    }

            .addOnFailureListener { e ->
                showLoading(false)
                when {
                    e.message?.contains("email address is already in use") == true ->
                        showError("This email is already registered")

                    e.message?.contains("badly formatted") == true ->
                        showError("Invalid email format")

                    else ->
                        showError("Registration failed: ${e.message}")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.registerBtn.isEnabled = false
            binding.registerBtn.text = ""
            binding.registerProgress.visibility = View.VISIBLE
        } else {
            binding.registerBtn.isEnabled = true
            binding.registerBtn.text = "Create Account"
            binding.registerProgress.visibility = View.GONE
        }
    }

    private fun showError(msg: String) {
        binding.errorTxt.text = msg
        binding.errorTxt.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.errorTxt.visibility = View.GONE
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
