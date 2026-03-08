package com.example.coffeeshop.Helper

import android.content.Context
import com.example.coffeeshop.domain.OrderModel

class ManagmentOrder(val context: Context) {

    private val tinyDB = TinyDB(context)

    fun insertOrder(order: OrderModel) {
        val list = getOrderList()
        list.add(0, order)
        tinyDB.putListGeneric("OrderList", list)
    }

    fun getOrderList(): ArrayList<OrderModel> {
        return tinyDB.getListGeneric("OrderList", OrderModel::class.java)
    }

    fun getOrderCount(): Int = getOrderList().size
}
