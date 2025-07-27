package com.example.projettdm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class AddReviewPageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val restaurantId = intent.getStringExtra("restaurantId") ?: ""
        setContent {
            AddReviewPage(restaurantId = restaurantId, context = this)
        }
    }
}
