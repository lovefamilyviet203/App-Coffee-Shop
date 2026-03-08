package com.example.coffeeshop.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeeshop.databinding.ViewholderOrderItemBinding
import com.example.coffeeshop.domain.ItemsModel

class OrderItemAdapter(
    private val items: ArrayList<ItemsModel>,
    private val context: Context
) : RecyclerView.Adapter<OrderItemAdapter.Viewholder>() {

    class Viewholder(val binding: ViewholderOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val binding = ViewholderOrderItemBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            titleOrderItemTxt.text = item.title
            priceOrderItemTxt.text = "$${"%.2f".format(item.price)}"
            qtyOrderItemTxt.text = "x${item.numberInCart}"

            // Show extra info if available
            if (item.extra.isNotEmpty()) {
                extraOrderItemTxt.text = item.extra
                extraOrderItemTxt.visibility = android.view.View.VISIBLE
            } else {
                extraOrderItemTxt.visibility = android.view.View.GONE
            }

            // Load image
            if (item.picUrl.isNotEmpty()) {
                Glide.with(context)
                    .load(item.picUrl[0])
                    .into(picOrderItem)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
