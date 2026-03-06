package com.example.coffeeshop.activities

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import com.example.coffeeshop.Helper.ManagmentCart
import com.example.coffeeshop.Helper.TinyDB
import com.example.coffeeshop.databinding.ActivityPaymentBinding
import com.example.coffeeshop.domain.ItemsModel

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var managmentCart: ManagmentCart
    private lateinit var tinyDB: TinyDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        managmentCart = ManagmentCart(this)
        tinyDB = TinyDB(this)

        loadSummary()
        setupPaymentOptions()
        loadSavedAddress()

        binding.backBtn.setOnClickListener { finish() }

        binding.payNowBtn.setOnClickListener {
            processPayment()
        }
    }

    private fun loadSavedAddress() {
        val savedAddress = tinyDB.getString("profile_address")
        if (savedAddress.isNotEmpty()) {
            binding.addressInput.setText(savedAddress)
        }
    }

    private fun loadSummary() {
        val subtotal = managmentCart.getTotalFee()
        val delivery = 10.0
        val tax = (subtotal * 0.02 * 100) / 100
        val total = ((subtotal + tax + delivery) * 100) / 100

        binding.summarySubtotalTxt.text = "$${"%.2f".format(subtotal)}"
        binding.summaryDeliveryTxt.text = "$$delivery"
        binding.summaryTaxTxt.text = "$${"%.2f".format(tax)}"
        binding.summaryTotalTxt.text = "$${"%.2f".format(total)}"
    }

    private fun setupPaymentOptions() {
        // Radio group behavior manually
        binding.radioCard.isChecked = true
        binding.cardFieldsLayout.visibility = android.view.View.VISIBLE

        binding.radioCard.setOnClickListener {
            binding.radioCard.isChecked = true
            binding.radioCash.isChecked = false
            binding.radioWallet.isChecked = false
            binding.cardFieldsLayout.visibility = android.view.View.VISIBLE
        }
        binding.cardOption.setOnClickListener {
            binding.radioCard.isChecked = true
            binding.radioCash.isChecked = false
            binding.radioWallet.isChecked = false
            binding.cardFieldsLayout.visibility = android.view.View.VISIBLE
        }
        binding.radioCash.setOnClickListener {
            binding.radioCard.isChecked = false
            binding.radioCash.isChecked = true
            binding.radioWallet.isChecked = false
            binding.cardFieldsLayout.visibility = android.view.View.GONE
        }
        binding.cashOption.setOnClickListener {
            binding.radioCard.isChecked = false
            binding.radioCash.isChecked = true
            binding.radioWallet.isChecked = false
            binding.cardFieldsLayout.visibility = android.view.View.GONE
        }
        binding.radioWallet.setOnClickListener {
            binding.radioCard.isChecked = false
            binding.radioCash.isChecked = false
            binding.radioWallet.isChecked = true
            binding.cardFieldsLayout.visibility = android.view.View.GONE
        }
        binding.walletOption.setOnClickListener {
            binding.radioCard.isChecked = false
            binding.radioCash.isChecked = false
            binding.radioWallet.isChecked = true
            binding.cardFieldsLayout.visibility = android.view.View.GONE
        }
    }

    private fun processPayment() {
        val address = binding.addressInput.text.toString().trim()
        if (address.isEmpty()) {
            binding.addressInput.error = "Please enter delivery address"
            return
        }

        if (binding.radioCard.isChecked) {
            val cardNumber = binding.cardNumberInput.text.toString().trim()
            val expiry = binding.cardExpiryInput.text.toString().trim()
            val cvv = binding.cardCvvInput.text.toString().trim()
            if (cardNumber.length < 16) {
                binding.cardNumberInput.error = "Enter valid card number"
                return
            }
            if (expiry.isEmpty()) {
                binding.cardExpiryInput.error = "Enter expiry"
                return
            }
            if (cvv.length < 3) {
                binding.cardCvvInput.error = "Enter valid CVV"
                return
            }
        }

        // Save order count
        val currentCount = tinyDB.getInt("order_count")
        tinyDB.putInt("order_count", currentCount + 1)

        // Clear cart
        tinyDB.putListObject("CartList", arrayListOf<ItemsModel>())

        // Generate order ID
        val orderId = "ORD${System.currentTimeMillis().toString().takeLast(6)}"

        val intent = Intent(this, OrderSuccessActivity::class.java)
        intent.putExtra("order_id", orderId)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}
