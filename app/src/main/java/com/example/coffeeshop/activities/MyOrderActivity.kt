package com.example.coffeeshop.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshop.Helper.FirebaseOrderHelper
import com.example.coffeeshop.Helper.TinyDB
import com.example.coffeeshop.adapters.OrderAdapter
import com.example.coffeeshop.databinding.ActivityMyOrderBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MyOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyOrderBinding
    private lateinit var tinyDB: TinyDB
    private var orderAdapter: OrderAdapter? = null

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
                    orderAdapter = OrderAdapter(ArrayList(orders), this)
                    binding.ordersRecyclerView.adapter = orderAdapter

                    // ✅ Gắn swipe sau khi set adapter
                    setupSwipeToDelete()
                }
            }
    }

    private fun setupSwipeToDelete() {
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            // ✅ Chỉ cho phép swipe đơn Cancelled
            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val position = viewHolder.adapterPosition
                val order = orderAdapter?.getItem(position)
                return if (order?.status == "Cancelled") {
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                } else {
                    0 // Block swipe với đơn khác
                }
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val order = orderAdapter?.getItem(position) ?: return

                // Xóa UI trước
                orderAdapter?.removeItem(position)

                // Cập nhật count
                val remaining = orderAdapter?.itemCount ?: 0
                if (remaining == 0) {
                    showEmpty()
                } else {
                    binding.orderCountSummaryTxt.text =
                        "$remaining order${if (remaining > 1) "s" else ""}"
                }

                // ✅ Snackbar hoàn tác trong 4 giây
                Snackbar.make(binding.root, "Đã xóa đơn #${order.orderId}", Snackbar.LENGTH_LONG)
                    .setAction("Hoàn tác") {
                        orderAdapter?.restoreItem(order, position)
                        val restored = orderAdapter?.itemCount ?: 0
                        binding.emptyLayout.visibility = View.GONE
                        binding.ordersRecyclerView.visibility = View.VISIBLE
                        binding.orderCountSummaryTxt.text =
                            "$restored order${if (restored > 1) "s" else ""}"
                    }
                    .addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(snackbar: Snackbar, event: Int) {
                            // Chỉ xóa Firebase nếu KHÔNG bấm Hoàn tác
                            if (event != DISMISS_EVENT_ACTION) {
                                FirebaseDatabase.getInstance().reference
                                    .child("Orders")
                                    .child(order.orderId)
                                    .removeValue()
                            }
                        }
                    })
                    .show()
            }
        }

        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.ordersRecyclerView)
    }

    private fun showEmpty() {
        binding.emptyLayout.visibility = View.VISIBLE
        binding.ordersRecyclerView.visibility = View.GONE
        binding.orderCountSummaryTxt.text = "0 orders"
    }
}