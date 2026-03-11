package com.example.coffeeshop.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import com.example.coffeeshop.Helper.ManagmentUser
import com.example.coffeeshop.Helper.TinyDB
import com.example.coffeeshop.databinding.ActivityLoginBinding

import com.example.coffeeshop.service.UserFcmService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var tinyDB: TinyDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tinyDB = TinyDB(this)

        // If already logged in, skip to MainActivity
        if (FirebaseAuth.getInstance().currentUser != null) {
            goToMain()
            return
        }

        setupClicks()
    }

    private fun setupClicks() {
        binding.loginBtn.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString()

            if (!validate(email, password)) return@setOnClickListener

            hideError()
            binding.loginBtn.isEnabled = false

            // ✅ Đăng nhập bằng Firebase Auth
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: return@addOnSuccessListener

                    // ✅ Lấy thông tin user từ Realtime Database lưu vào TinyDB
                    FirebaseDatabase.getInstance().reference
                        .child("Users")
                        .child(uid)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            tinyDB.putString("profile_name", snapshot.child("name").value?.toString() ?: "")
                            tinyDB.putString("profile_email", snapshot.child("email").value?.toString() ?: email)
                            tinyDB.putString("profile_phone", snapshot.child("phone").value?.toString() ?: "")

                            UserFcmService.registerToken(this)
                            goToMain()
                        }
                        .addOnFailureListener {
                            // DB lỗi nhưng Auth OK → vẫn cho vào app
                            tinyDB.putString("profile_email", email)
                            UserFcmService.registerToken(this)
                            goToMain()
                        }
                }
                .addOnFailureListener { e ->
                    binding.loginBtn.isEnabled = true
                    when {
                        e.message?.contains("no user record") == true ||
                                e.message?.contains("password is invalid") == true ||
                                e.message?.contains("INVALID_LOGIN_CREDENTIALS") == true ->
                            showError("Incorrect email or password")
                        e.message?.contains("badly formatted") == true ->
                            showError("Invalid email format")
                        else ->
                            showError("Login failed: ${e.message}")
                    }
                }
        }

        binding.goToRegisterBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.continueAsGuestTxt.setOnClickListener {
            goToMain()
        }
    }

    private fun validate(email: String, password: String): Boolean {
        hideError()
        if (email.isEmpty()) {
            binding.emailInput.error = "Please enter your email"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInput.error = "Enter a valid email"
            return false
        }
        if (password.isEmpty()) {
            binding.passwordInput.error = "Please enter your password"
            return false
        }
        return true
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
