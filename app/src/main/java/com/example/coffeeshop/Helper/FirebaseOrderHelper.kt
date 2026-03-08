package com.example.coffeeshop.Helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coffeeshop.domain.OrderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FirebaseOrderHelper {

    private val db = FirebaseDatabase.getInstance().getReference("Orders")

    /** Save order to Firebase and return success/failure */
    fun saveOrder(order: OrderModel, onResult: (Boolean) -> Unit) {
        db.child(order.orderId).setValue(order)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    /** Save shop's FCM token so user app knows where to send (for future use) */
    fun saveUserFcmToken(userEmail: String, token: String) {
        val safeEmail = userEmail.replace(".", "_").replace("@", "_at_")
        FirebaseDatabase.getInstance()
            .getReference("UserFcmTokens")
            .child(safeEmail)
            .setValue(token)
    }

    /** Listen to orders for a specific user — realtime updates */
    fun listenUserOrders(userEmail: String): LiveData<List<OrderModel>> {
        val liveData = MutableLiveData<List<OrderModel>>()

        db.orderByChild("userEmail").equalTo(userEmail)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<OrderModel>()
                    for (child in snapshot.children) {
                        child.getValue(OrderModel::class.java)?.let { list.add(it) }
                    }
                    // Sort newest first
                    list.sortByDescending { it.timestamp }
                    liveData.value = list
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        return liveData
    }
}
