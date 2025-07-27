package com.example.projettdm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable

class ViewCommentsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val restaurantId = intent.getStringExtra("restaurantId") ?: ""
        setContent {
            ViewCommentsPage(restaurantId = restaurantId, context = this)
        }
    }
}
