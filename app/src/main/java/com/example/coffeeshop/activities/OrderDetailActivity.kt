package com.example.coffeeshop.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeshop.adapters.OrderItemAdapter
import com.example.coffeeshop.databinding.ActivityOrderDetailBinding
import com.example.coffeeshop.domain.OrderModel
import com.google.firebase.database.FirebaseDatabase
import android.view.View

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding
    private var currentOrder: OrderModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val order = intent.getSerializableExtra("order") as? OrderModel
        if (order != null) {
            currentOrder = order
            bindOrderData(order)
        }

        binding.backBtn.setOnClickListener { finish() }
        binding.cancelOrderBtn.setOnClickListener {
            showCancelConfirmDialog()
        }
    }

    private fun bindOrderData(order: OrderModel) {
        binding.apply {
            // Header info
            detailOrderIdTxt.text = "Order #${order.orderId}"
            detailDateTxt.text = order.dateTime
            detailStatusBadge.text = order.status

            detailStatusBadge.setTextColor(getStatusColor(order.status))

            // Delivery info
            detailAddressTxt.text = order.address
            detailPaymentTxt.text = when (order.paymentMethod) {
                "Cash" -> "Cash on Delivery"
                "Wallet" -> "Digital Wallet"
                else -> "Credit / Debit Card"
            }

            // Price summary
            detailSubtotalTxt.text = "$${"%.2f".format(order.totalFee)}"
            detailDeliveryTxt.text = "$${"%.2f".format(order.deliveryFee)}"
            detailTaxTxt.text = "$${"%.2f".format(order.tax)}"
            detailTotalTxt.text = "$${"%.2f".format(order.grandTotal)}"

            // Items list
            orderItemsRecyclerView.layoutManager = LinearLayoutManager(this@OrderDetailActivity)
            orderItemsRecyclerView.adapter = OrderItemAdapter(order.items, this@OrderDetailActivity)

            if (canCancelOrder(order.status)) {
                cancelOrderBtn.visibility = View.VISIBLE
            } else {
                cancelOrderBtn.visibility = View.GONE
            }
        }
    }
    private fun canCancelOrder(status: String): Boolean {
        return status == "Pending" || status == "Processing"
    }

    private fun getStatusColor(status: String): Int {
        return when (status) {
            "Pending"    -> android.graphics.Color.parseColor("#FFA500") // cam
            "Processing" -> android.graphics.Color.parseColor("#3B82F6") // xanh dương
            "Shipped"    -> android.graphics.Color.parseColor("#8B5CF6") // tím
            "Delivered"  -> android.graphics.Color.parseColor("#22C55E") // xanh lá
            "Cancelled"  -> android.graphics.Color.parseColor("#EF4444") // đỏ
            else         -> android.graphics.Color.parseColor("#FFA500")
        }
    }

    private fun showCancelConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("Hủy đơn hàng")
            .setMessage("Bạn có chắc muốn hủy đơn hàng này không?\nHành động này không thể hoàn tác.")
            .setPositiveButton("Hủy đơn") { _, _ ->
                performCancelOrder()
            }
            .setNegativeButton("Giữ lại", null)
            .show()
    }
    private fun performCancelOrder() {
        val order = currentOrder ?: return

        binding.cancelOrderBtn.isEnabled = false
        binding.cancelOrderBtn.text = "Đang hủy..."

        FirebaseDatabase.getInstance().reference
            .child("Orders")
            .child(order.orderId)
            .child("status")
            .setValue("Cancelled")
            .addOnSuccessListener {
                Toast.makeText(this, "Đơn hàng đã được hủy", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                binding.cancelOrderBtn.isEnabled = true
                binding.cancelOrderBtn.text = "Hủy đơn hàng"
                Toast.makeText(this, "Không thể hủy đơn. Vui lòng thử lại!", Toast.LENGTH_SHORT).show()
            }
    }
}
