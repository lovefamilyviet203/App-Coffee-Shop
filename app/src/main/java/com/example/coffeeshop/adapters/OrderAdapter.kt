package com.example.coffeeshop.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshop.activities.OrderDetailActivity
import com.example.coffeeshop.databinding.ViewholderOrderBinding
import com.example.coffeeshop.domain.OrderModel

class OrderAdapter(
    private val orders: ArrayList<OrderModel>,
    private val context: Context
) : RecyclerView.Adapter<OrderAdapter.Viewholder>() {

    class Viewholder(val binding: ViewholderOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val binding = ViewholderOrderBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val order = orders[position]
        holder.binding.apply {

            orderIdTxt.text = "Order #${order.orderId}"
            orderDateTxt.text = order.dateTime
            statusBadge.text = order.status
            orderAddressTxt.text = "📍 ${order.address}"
            orderTotalTxt.text = "Total: $${"%.2f".format(order.grandTotal)}"

            // Payment method icon
            orderPaymentTxt.text = when (order.paymentMethod) {
                "Cash" -> "💵 Cash on Delivery"
                "Wallet" -> "👛 Digital Wallet"
                else -> "💳 Card"
            }

            // Build items summary string
            val itemCount = order.items.sumOf { it.numberInCart }
            val itemsSummary = order.items.joinToString(", ") { "${it.title} x${it.numberInCart}" }
            orderItemsTxt.text = "• $itemsSummary"

            // Click to open Order Detail
            root.setOnClickListener {
                val intent = Intent(context, OrderDetailActivity::class.java)
                intent.putExtra("order", order)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = orders.size
}
