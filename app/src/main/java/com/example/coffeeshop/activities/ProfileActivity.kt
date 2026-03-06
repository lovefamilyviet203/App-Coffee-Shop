package com.example.coffeeshop.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import com.example.coffeeshop.Helper.ManagmentWishlist
import com.example.coffeeshop.Helper.TinyDB
import com.example.coffeeshop.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var tinyDB: TinyDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tinyDB = TinyDB(this)

        loadProfile()
        loadStats()

        binding.backBtn.setOnClickListener { finish() }

        binding.saveProfileBtn.setOnClickListener {
            saveProfile()
        }
    }

    private fun loadProfile() {
        binding.apply {
            val name = tinyDB.getString("profile_name").ifEmpty { "Tina Anderson" }
            val email = tinyDB.getString("profile_email").ifEmpty { "tina@example.com" }
            val phone = tinyDB.getString("profile_phone")
            val address = tinyDB.getString("profile_address")

            profileNameTxt.text = name
            profileEmailTxt.text = email
            editName.setText(name)
            editEmail.setText(email)
            editPhone.setText(phone)
            editAddress.setText(address)
        }
    }

    private fun loadStats() {
        val wishCount = ManagmentWishlist(this).getListWish().size
        binding.wishlistCountTxt.text = wishCount.toString()
        val orderCount = tinyDB.getInt("order_count")
        binding.orderCountTxt.text = orderCount.toString()
    }

    private fun saveProfile() {
        val name = binding.editName.text.toString().trim()
        val email = binding.editEmail.text.toString().trim()
        val phone = binding.editPhone.text.toString().trim()
        val address = binding.editAddress.text.toString().trim()

        if (name.isEmpty()) {
            binding.editName.error = "Please enter your name"
            return
        }

        tinyDB.putString("profile_name", name)
        tinyDB.putString("profile_email", email)
        tinyDB.putString("profile_phone", phone)
        tinyDB.putString("profile_address", address)

        binding.profileNameTxt.text = name
        binding.profileEmailTxt.text = email

        Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()
    }
}
