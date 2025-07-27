package com.example.projettdm

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddReviewPage(restaurantId: String, context: Context) {
    val context = LocalContext.current
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    val restaurant = getRestaurantFromDatabase(restaurantId)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp), // Reserve space for BottomNavBar
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Box(modifier = Modifier.height(200.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.restaurant_main_image),
                    contentDescription = "Main Restaurant Image",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }

            // Restaurant Name
            Text(
                text = restaurant.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            // Rating Section
            Text(
                text = "Give a rating from 1 to 5",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(16.dp)
            )

            Row(modifier = Modifier.padding(8.dp)) {
                for (i in 1..5) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star_filled),
                        contentDescription = "Star",
                        tint = if (i <= rating) Color(0xFFF56949) else Color(0xFFCEC9C1),
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                rating = if (rating == i) i - 1 else i
                            }
                    )
                }
            }

            // Comment Section
            Text(
                text = "Give a comment",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(16.dp)
            )

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Write your comment here") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    if (rating > 0 && comment.isNotBlank()) {
                        Toast.makeText(
                            context,
                            "Review submitted successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Handle review submission logic here
                    } else {
                        Toast.makeText(
                            context,
                            "Please provide a rating and a comment",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF56949)),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .width(363.dp)
                    .height(45.dp)
            ) {
                Text(
                    text = "Send",
                    color = Color.White,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )
            }
        }

        // Bottom navigation bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            AddReviewBottomNavBar()
        }
    }
}

@Composable
fun AddReviewBottomNavBar() {
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
                                "Home" -> R.drawable.ic_home
                                "Search" -> R.drawable.ic_search
                                "Order" -> R.drawable.ic_order
                                "Profile" -> R.drawable.ic_profile
                                else -> R.drawable.ic_home
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
