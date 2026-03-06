package com.example.coffeeshop.Helper

import android.content.Context
import android.widget.Toast
import com.example.coffeeshop.domain.ItemsModel

class ManagmentWishlist(val context: Context) {

    private val tinyDB = TinyDB(context)

    fun insertWishItem(item: ItemsModel) {
        var listItem = getListWish()
        val existAlready = listItem.any { it.title == item.title }
        if (!existAlready) {
            listItem.add(item)
            tinyDB.putListObject("WishList", listItem)
            Toast.makeText(context, "Added to Wishlist", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Already in Wishlist", Toast.LENGTH_SHORT).show()
        }
    }

    fun removeWishItem(listItems: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        listItems.removeAt(position)
        tinyDB.putListObject("WishList", listItems)
        listener.onChanged()
    }

    fun getListWish(): ArrayList<ItemsModel> {
        return tinyDB.getListObject("WishList") ?: arrayListOf()
    }

    fun isInWishlist(item: ItemsModel): Boolean {
        return getListWish().any { it.title == item.title }
    }
}
