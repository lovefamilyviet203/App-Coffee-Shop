package com.example.coffeeshop.activities

import android.net.Uri
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.bumptech.glide.Glide
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.coffeeshop.Helper.ManagmentWishlist
import com.example.coffeeshop.Helper.TinyDB
import com.example.coffeeshop.R
import com.example.coffeeshop.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var tinyDB: TinyDB
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                selectedImageUri = uri
                // Hiển thị ảnh preview ngay
                Glide.with(this).load(uri).circleCrop().into(binding.profileAvatar)
                // Lưu URI vào TinyDB
                tinyDB.putString("profile_avatar_uri", uri.toString())
                Toast.makeText(this, "Ảnh đã được cập nhật!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Launcher để xin quyền đọc ảnh
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) openGallery()
        else Toast.makeText(this, "Cần quyền truy cập ảnh", Toast.LENGTH_SHORT).show()
    }

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
        val name = tinyDB.getString("profile_name").ifEmpty { "User" }
        val email = FirebaseAuth.getInstance().currentUser?.email
            ?: tinyDB.getString("profile_email").ifEmpty { "" }
        val phone = tinyDB.getString("profile_phone")
        val address = tinyDB.getString("profile_address")
        val avatarUri = tinyDB.getString("profile_avatar_uri")

        binding.profileNameTxt.text = name
        binding.profileEmailTxt.text = email
        binding.editName.setText(name)
        binding.editEmail.setText(email)
        binding.editPhone.setText(phone)
        binding.editAddress.setText(address)

        // Load ảnh đại diện đã lưu
        if (avatarUri.isNotEmpty()) {
            Glide.with(this)
                .load(Uri.parse(avatarUri))
                .circleCrop()
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .into(binding.profileAvatar)
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

    private fun checkPermissionAndOpenGallery() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }
}
