package com.example.coffeeshop.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.coffeeshop.Helper.FirebaseOrderHelper
import com.example.coffeeshop.Helper.TinyDB
import com.example.coffeeshop.R
import com.example.coffeeshop.activities.MyOrderActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class UserFcmService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Save new token to Firebase so shop can notify this user
        val email = TinyDB(this).getString("profile_email")
        if (email.isNotEmpty()) {
            FirebaseOrderHelper.saveUserFcmToken(email, token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: message.data["title"] ?: "Coffee Shop"
        val body = message.notification?.body ?: message.data["body"] ?: "Your order has been updated"

        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "order_status_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Order Status Updates",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MyOrderActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        /** Call this after login to register/refresh FCM token */
        fun registerToken(context: Context) {
            val email = TinyDB(context).getString("profile_email")
            if (email.isEmpty()) return
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                FirebaseOrderHelper.saveUserFcmToken(email, token)
            }
        }
    }
}
