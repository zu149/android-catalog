package com.example.registerfirebasebinding.models

import com.google.firebase.Timestamp

data class users (
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val img: String = "",
    val pol: String = "",
    val pay_month: Int = 0,
    val pay_year: Int = 0,
    val date: Timestamp = Timestamp.now()


    )
