package com.example.coffeeshop.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import com.example.coffeeshop.Helper.ManagmentUser
import com.example.coffeeshop.databinding.ActivityLoginBinding

import com.example.coffeeshop.service.UserFcmService

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var managmentUser: ManagmentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentUser = ManagmentUser(this)

        // If already logged in, skip to MainActivity
        if (managmentUser.isLoggedIn()) {
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

            val success = managmentUser.login(email, password)
            if (success) {
                UserFcmService.registerToken(this)
                goToMain()
            } else {
                showError("Incorrect email or password")
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
