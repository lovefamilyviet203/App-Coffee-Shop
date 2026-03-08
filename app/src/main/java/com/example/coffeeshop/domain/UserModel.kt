package com.example.coffeeshop.domain

import java.io.Serializable

data class UserModel(
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var password: String = ""
) : Serializable
