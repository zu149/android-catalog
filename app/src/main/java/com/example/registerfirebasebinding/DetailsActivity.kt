package com.example.registerfirebasebinding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.registerfirebasebinding.fragments.DetailsFragment

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val priem = intent.extras?.getInt("appid", 0) // получение id oт view

        val detailsFragment = supportFragmentManager.findFragmentById(R.id.fragment_details) as DetailsFragment

        detailsFragment.setDetail(priem!!)

    }
}