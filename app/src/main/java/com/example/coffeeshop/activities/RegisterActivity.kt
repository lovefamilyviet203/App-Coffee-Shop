package com.example.coffeeshop.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import com.example.coffeeshop.Helper.ManagmentUser
import com.example.coffeeshop.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var managmentUser: ManagmentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentUser = ManagmentUser(this)

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

        // Attempt register
        val result = managmentUser.register(name, email, phone, password)

        when (result) {
            ManagmentUser.RegisterResult.SUCCESS -> {
                // Auto-login after register
                managmentUser.login(email, password)
                goToMain()
            }
            ManagmentUser.RegisterResult.EMAIL_EXISTS -> {
                binding.emailInput.error = "This email is already registered"
                showError("An account with this email already exists")
            }
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
