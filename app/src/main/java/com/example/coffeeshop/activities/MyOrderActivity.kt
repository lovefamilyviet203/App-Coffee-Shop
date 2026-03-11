package com.example.coffeeshop.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeshop.Helper.FirebaseOrderHelper
import com.example.coffeeshop.Helper.ManagmentOrder
import com.example.coffeeshop.Helper.TinyDB
import com.example.coffeeshop.adapters.OrderAdapter
import com.example.coffeeshop.databinding.ActivityMyOrderBinding
import com.google.firebase.auth.FirebaseAuth

class MyOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyOrderBinding
    private lateinit var tinyDB: TinyDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tinyDB = TinyDB(this)
        binding.backBtn.setOnClickListener { finish() }

        loadOrdersFromFirebase()
    }

    private fun loadOrdersFromFirebase() {
        val auth = FirebaseAuth.getInstance()
        val userEmail = auth.currentUser?.email
            ?: tinyDB.getString("profile_email")

        if (userEmail.isEmpty()) {
            showEmpty()
            return
        }

        binding.orderCountSummaryTxt.text = "Loading..."

        FirebaseOrderHelper.listenUserOrders(userEmail)
            .observe(this) { orders ->
                if (orders.isEmpty()) {
                    showEmpty()
                } else {
                    binding.emptyLayout.visibility = View.GONE
                    binding.ordersRecyclerView.visibility = View.VISIBLE
                    val count = orders.size
                    binding.orderCountSummaryTxt.text = "$count order${if (count > 1) "s" else ""}"

                    binding.ordersRecyclerView.layoutManager = LinearLayoutManager(this)
                    binding.ordersRecyclerView.adapter = OrderAdapter(ArrayList(orders), this)
                }
            }
    }

    private fun showEmpty() {
        binding.emptyLayout.visibility = View.VISIBLE
        binding.ordersRecyclerView.visibility = View.GONE
        binding.orderCountSummaryTxt.text = "0 orders"
    }
}
