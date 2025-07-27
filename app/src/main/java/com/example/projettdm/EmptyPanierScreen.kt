package com.example.projettdm

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmptyCartPage(context: Context) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Force background to white to handle dark mode
    ) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp), // Reserve space for BottomNavBar
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Back Arrow Icon
                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back Arrow",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterStart) // Align to the start of the row
                        .clickable {
                            // Handle back navigation
                        }
                )

                // Title "My Cart"
                Text(
                    text = "My Cart",
                    style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Bold),
                    color = Color.Black, // Explicit color to display in dark mode
                    modifier = Modifier.align(Alignment.Center) // Center horizontally in the box
                )
            }

            // Empty Cart Content
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ellipse_paniervide),
                    contentDescription = "Empty Cart Illustration",
                    modifier = Modifier.size(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Empty",
                    style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You don’t have any foods in your cart, get some foods!",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                    color = Color.Gray
                )
            }
        }

        // Bottom navigation bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter) // Align explicitly in BoxScope
        ) {
            CartBottomNavBar()
        }
    }
}

@Composable
fun CartBottomNavBar() {
    val items = listOf("Home", "Search", "Order", "Profile")
    var selectedIndex by remember { mutableStateOf(0) }

    BottomNavigation(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0xFFEFF3FC)
    ) {
        items.forEachIndexed { index, item ->
            BottomNavigationItem(
                icon = {
                    Image(
                        painter = painterResource(
                            id = when (item) {
                                "Home" -> R.drawable.ic_homepanier
                                "Search" -> R.drawable.ic_search
                                "Order" -> R.drawable.ic_orderpanier
                                "Profile" -> R.drawable.ic_profile
                                else -> R.drawable.ic_orderpanier
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp)
                    )
                },
                selected = selectedIndex == index,
                onClick = { selectedIndex = index },
                alwaysShowLabel = false
            )
        }
    }
}
