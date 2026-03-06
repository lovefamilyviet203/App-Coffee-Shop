package com.example.coffeeshop.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeshop.Helper.ChangeNumberItemsListener
import com.example.coffeeshop.Helper.ManagmentWishlist
import com.example.coffeeshop.adapters.WishlistAdapter
import com.example.coffeeshop.databinding.ActivityWishlistBinding

class WishlistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWishlistBinding
    private lateinit var managmentWishlist: ManagmentWishlist

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        managmentWishlist = ManagmentWishlist(this)

        binding.backBtn.setOnClickListener { finish() }
        loadWishlist()
    }

    override fun onResume() {
        super.onResume()
        loadWishlist()
    }

    private fun loadWishlist() {
        val list = managmentWishlist.getListWish()
        if (list.isEmpty()) {
            binding.emptyWishTxt.visibility = View.VISIBLE
            binding.wishlistView.visibility = View.GONE
        } else {
            binding.emptyWishTxt.visibility = View.GONE
            binding.wishlistView.visibility = View.VISIBLE
            binding.wishlistView.layoutManager = LinearLayoutManager(this)
            binding.wishlistView.adapter = WishlistAdapter(list, this, object : ChangeNumberItemsListener {
                override fun onChanged() {
                    loadWishlist()
                }
            })
        }
    }
}
