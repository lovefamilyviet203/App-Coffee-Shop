package com.example.coffeeshop.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.bumptech.glide.Glide
import com.example.coffeeshop.Helper.ManagmentCart
import com.example.coffeeshop.R
import com.example.coffeeshop.databinding.ActivityDetailBinding
import com.example.coffeeshop.domain.ItemsModel

class DetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var managmentCart: ManagmentCart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)
        bundle()
        initSizeList()
    }

    private fun initSizeList() {

        binding.SmallBtn.setBackgroundResource(R.drawable.brown_full_corner_bg)

        binding.apply {
            SmallBtn.setOnClickListener {
                SmallBtn.setBackgroundResource(R.drawable.brown_full_corner_bg)
                MediumBtn.setBackgroundResource(0)
                LargeBtn.setBackgroundResource(0)
            }
            MediumBtn.setOnClickListener {
                SmallBtn.setBackgroundResource(0)
                MediumBtn.setBackgroundResource(R.drawable.brown_full_corner_bg)
                LargeBtn.setBackgroundResource(0)
            }
            LargeBtn.setOnClickListener {
                MediumBtn.setBackgroundResource(0)
                SmallBtn.setBackgroundResource(0)
                LargeBtn.setBackgroundResource(R.drawable.brown_full_corner_bg)
            }
        }
    }

    private fun bundle() {
        binding.apply {
            item = intent.getSerializableExtra("object") as ItemsModel

            Glide.with(this@DetailActivity)
                .load(item.picUrl[0])
                .into(binding.picMain)

            titleTxt.text = item.title
            descriptionTxt.text = item.description
            priceTxt.text ="$" + item.price
            ratingTxt.text = item.rating.toString()

            addToCartBtn.setOnClickListener{
                item.numberInCart = Integer.valueOf(
                    numberInCartTxt.text.toString()
                )
                managmentCart.insertItems(item)
            }

            backBtn.setOnClickListener{ finish() }

            plusBtn.setOnClickListener {
                numberInCartTxt.text = (item.numberInCart + 1).toString()
                item.numberInCart++
            }

            minusBtn.setOnClickListener {
                if (item.numberInCart > 0)
                {
                    numberInCartTxt.text = (item.numberInCart - 1).toString()
                    item.numberInCart--
                }
            }
        }
    }
}