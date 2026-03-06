package com.example.coffeeshop.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeeshop.Helper.ChangeNumberItemsListener
import com.example.coffeeshop.Helper.ManagmentWishlist
import com.example.coffeeshop.activities.DetailActivity
import com.example.coffeeshop.databinding.ViewholderWishlistBinding
import com.example.coffeeshop.domain.ItemsModel

class WishlistAdapter(
    private val items: ArrayList<ItemsModel>,
    private val context: Context,
    private val listener: ChangeNumberItemsListener
) : RecyclerView.Adapter<WishlistAdapter.Viewholder>() {

    private val managmentWishlist = ManagmentWishlist(context)

    class Viewholder(val binding: ViewholderWishlistBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val binding = ViewholderWishlistBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            titleWishTxt.text = item.title
            priceWishTxt.text = "$${item.price}"
            ratingWishTxt.text = "⭐ ${item.rating}"

            Glide.with(context)
                .load(item.picUrl[0])
                .into(picWish)

            deleteWishBtn.setOnClickListener {
                managmentWishlist.removeWishItem(items, holder.adapterPosition, listener)
                notifyItemRemoved(holder.adapterPosition)
            }

            root.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("object", item)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
