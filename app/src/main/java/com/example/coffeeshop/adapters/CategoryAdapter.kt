package com.example.coffeeshop.adapters

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshop.R
import com.example.coffeeshop.activities.ItemsListActivity
import com.example.coffeeshop.databinding.ViewholderCategoryBinding
import com.example.coffeeshop.domain.CategoryModel


class CategoryAdapter(val items: MutableList<CategoryModel>):
RecyclerView.Adapter<CategoryAdapter.ViewHolder>()
{
    private lateinit var context: Context
    private var selectedPosition = -1
    private var lastSelectedPosition = -1
    class ViewHolder(val binding: ViewholderCategoryBinding):
    RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        context = parent.context
        val binding = ViewholderCategoryBinding.inflate(LayoutInflater.from(context), parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.titleCat.text = item.title

        holder.binding.root.setOnClickListener()
        {
            lastSelectedPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(lastSelectedPosition)
            notifyItemChanged(selectedPosition)

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(context, ItemsListActivity::class.java).apply {
                    putExtra("title", item.title)
                    putExtra("id", item.id.toString())
                }

                ContextCompat.startActivity(context, intent, null)
            }, 500)
        }

        if (selectedPosition == position)
        {
            holder.binding.titleCat.setBackgroundResource(R.drawable.brown_full_corner_bg)
        }
        else
        {
            holder.binding.titleCat.setBackgroundResource(R.drawable.brown_2_full_corner_bg)
        }
    }

    override fun getItemCount(): Int = items.size

}