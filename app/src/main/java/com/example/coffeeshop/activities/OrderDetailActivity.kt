package com.example.coffeeshop.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeshop.adapters.OrderItemAdapter
import com.example.coffeeshop.databinding.ActivityOrderDetailBinding
import com.example.coffeeshop.domain.OrderModel

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val order = intent.getSerializableExtra("order") as? OrderModel
        if (order != null) {
            bindOrderData(order)
        }

        binding.backBtn.setOnClickListener { finish() }
    }

    private fun bindOrderData(order: OrderModel) {
        binding.apply {
            // Header info
            detailOrderIdTxt.text = "Order #${order.orderId}"
            detailDateTxt.text = order.dateTime
            detailStatusBadge.text = order.status

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
        }
    }
}
