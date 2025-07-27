package com.example.projettdm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter

@Composable
fun RestaurantDetailsPage(restaurantId: String, context: Context) {
    val restaurant = getRestaurantFromDatabase(restaurantId)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header with main image and logo
        Box(modifier = Modifier.height(200.dp)) {
            Image(
                painter = painterResource(id = R.drawable.restaurant_main_image),
                contentDescription = "Main Restaurant Image",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Image(
                painter = rememberImagePainter(restaurant.logoUrl),
                contentDescription = "Restaurant Logo",
                modifier = Modifier
                    .size(80.dp)
                    .offset(y = 100.dp)
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(50))
                    .background(Color.LightGray)
                    .padding(8.dp)
            )
        }

        // Restaurant name
        Text(
            text = restaurant.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Drawable rectangle placeholder
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(8.dp)
                .padding(horizontal = 16.dp)
                .background(Color(0xFFF56949), shape = RoundedCornerShape(4.dp))
        )

        // Main information
        Column(modifier = Modifier.padding(16.dp)) {
            RestaurantInfoRow("Cuisine:", restaurant.cuisine)
            RestaurantInfoRow(
                "Rate:", "${restaurant.rating}",
                iconId = R.drawable.ic_star
            )
            RestaurantInfoRow("Reviews:", "${restaurant.reviewCount}")

            Text(
                text = "View Comments",
                color = Color(0xFFF56949),
                modifier = Modifier.clickable {
                    val intent = Intent(context, ViewCommentsActivity::class.java).apply {
                        putExtra("restaurantId", restaurantId)
                    }
                    if (context is Activity) {
                        context.startActivity(intent)
                    }
                }
            )
        }

        // Drawable rectangle placeholder
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(8.dp)
                .padding(horizontal = 16.dp)
                .background(Color(0xFFF56949), shape = RoundedCornerShape(4.dp))
        )

        // Contact information
        Column(modifier = Modifier.padding(16.dp)) {
            ClickableTextRow("Phone:", restaurant.phone) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${restaurant.phone}"))
                if (context is Activity) {
                    context.startActivity(intent)
                }
            }

            ClickableTextRow("E-mail:", restaurant.email) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:${restaurant.email}")
                }
                if (context is Activity) {
                    context.startActivity(intent)
                }
            }

            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Adresse:",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(80.dp)
                    )
                    Text(text = restaurant.address)
                }
                Text(
                    text = "Open maps",
                    color = Color(0xFFF56949),
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${restaurant.address}"))
                        if (context is Activity) {
                            context.startActivity(intent)
                        }
                    }
                )
            }
        }

        // Drawable rectangle placeholder
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(8.dp)
                .padding(horizontal = 16.dp)
                .background(Color(0xFFF56949), shape = RoundedCornerShape(4.dp))
        )

        // Social media links
        Text(
            text = "Links to social media",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SocialMediaIcon(R.drawable.ic_facebook2, "https://facebook.com", context)
            SocialMediaIcon(R.drawable.ic_instagram, "https://instagram.com", context)
            SocialMediaIcon(R.drawable.ic_playstore, "https://play.google.com/store", context)
            SocialMediaIcon(R.drawable.ic_applestore, "https://apple.com/app-store", context)
        }

        // Button "Check Menu"
        Button(
            onClick = {
                val intent = Intent(context, MenuPageActivity::class.java).apply {
                    putExtra("restaurantId", restaurantId)
                }
                if (context is Activity) {
                    context.startActivity(intent)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF56949)),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(text = "Check Menu", fontWeight = FontWeight.W400, color = Color.White)
        }

        // Bottom navigation bar
        BottomNavBar()
    }
}

@Composable
fun RestaurantInfoRow(label: String, value: String, iconId: Int? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp)
        )
        if (iconId != null) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Yellow
            )
        }
        Text(text = value)
    }
}

@Composable
fun ClickableTextRow(label: String, value: String, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            modifier = Modifier.clickable { onClick() }
        )
    }
}

@Composable
fun SocialMediaIcon(iconId: Int, url: String, context: Context) {
    Icon(
        painter = painterResource(id = iconId),
        contentDescription = null,
        modifier = Modifier
            .size(40.dp)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                if (context is Activity) {
                    context.startActivity(intent)
                }
            },
        tint = Color.Unspecified // Désactive la teinte pour afficher les couleurs originales
    )
}

fun getRestaurantFromDatabase(restaurantId: String): Restaurant {
    return Restaurant(
        logoUrl = "https://example.com/logo.png",
        name = "Le Gourmet",
        cuisine = "Française",
        rating = 4.5,
        reviewCount = 120,
        phone = "0123456789",
        email = "contact@legourmet.com",
        address = "123 Rue Principale, Algiers, Algeria"
    )
}

data class Restaurant(
    val logoUrl: String,
    val name: String,
    val cuisine: String,
    val rating: Double,
    val reviewCount: Int,
    val phone: String,
    val email: String,
    val address: String
)
