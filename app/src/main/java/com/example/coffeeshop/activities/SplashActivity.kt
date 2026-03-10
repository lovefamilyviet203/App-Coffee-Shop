package com.example.coffeeshop.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.example.coffeeshop.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startBtn.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser != null) {
                // Đã đăng nhập rồi → vào thẳng Main
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Chưa đăng nhập → vào Login
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }
    }
}