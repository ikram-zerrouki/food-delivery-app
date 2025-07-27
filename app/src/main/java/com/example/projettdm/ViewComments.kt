package com.example.projettdm

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
fun ViewCommentsPage(restaurantId: String, context: Context) {
    val restaurant = getRestaurantFromDatabase(restaurantId)
    val comments = remember { mutableStateListOf(*getCommentsForRestaurant(restaurantId).toTypedArray()) }

    Column(
        modifier = Modifier.fillMaxSize()
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

        // Title: Customer Reviews
        Text(
            text = "Customer Reviews",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Rating stars
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            val filledStars = restaurant.rating.toInt()
            val emptyStars = 5 - filledStars

            repeat(filledStars) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_star_filled),
                    contentDescription = null,
                    tint = Color(0xFFFFAC33),
                    modifier = Modifier.size(24.dp)
                )
            }

            repeat(emptyStars) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_star_empty),
                    contentDescription = null,
                    tint = Color(0xFFCEC9C1),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Button: Add a Rate and Review
        Button(
            onClick = {
                val intent = Intent(context, AddReviewPageActivity::class.java).apply {
                    putExtra("restaurantId", restaurantId)
                }
                if (context is Activity) {
                    context.startActivity(intent)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF56949)),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(45.dp),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(text = "+ Add a Rate and Review", color = Color.White)
        }

        // Title: See what others are saying
        Text(
            text = "See what others are saying",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Scrollable comments list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            items(comments.size) { index ->
                CommentItem(
                    comment = comments[index],
                    onLike = {
                        comments[index] = comments[index].copy(likes = comments[index].likes + 1)
                    },
                    onDislike = {
                        comments[index] = comments[index].copy(dislikes = comments[index].dislikes + 1)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Bottom navigation bar
        AddReviewBottomNavBar()
    }
}

@Composable
fun CommentItem(comment: Comment, onLike: () -> Unit, onDislike: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberImagePainter(comment.userProfileUrl),
                contentDescription = "User Profile",
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(text = comment.userName, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = comment.content)

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            val filledStars = comment.rating
            val emptyStars = 5 - filledStars

            repeat(filledStars) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_star_filled),
                    contentDescription = null,
                    tint = Color(0xFFFFAC33),
                    modifier = Modifier.size(16.dp)
                )
            }

            repeat(emptyStars) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_star_empty),
                    contentDescription = null,
                    tint = Color(0xFFCEC9C1),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Like (${comment.likes})",
                color = Color.Blue,
                modifier = Modifier.clickable(onClick = onLike)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Dislike (${comment.dislikes})",
                color = Color.Red,
                modifier = Modifier.clickable(onClick = onDislike)
            )
        }
    }
}

fun getCommentsForRestaurant(restaurantId: String): List<Comment> {
    return listOf(
        Comment(
            userProfileUrl = "https://example.com/user1.png",
            userName = "Amel Ben",
            content = "The service was amazing, and the food was absolutely delicious!",
            rating = 4,
            likes = 5,
            dislikes = 1
        ),
        Comment(
            userProfileUrl = "https://example.com/user2.png",
            userName = "Si Adam",
            content = "The delivery was late.",
            rating = 1,
            likes = 2,
            dislikes = 3
        ),
        Comment(
            userProfileUrl = "https://example.com/user3.png",
            userName = "Fatima Zohra",
            content = "Wonderful ambiance and tasty dishes. Highly recommended!",
            rating = 5,
            likes = 20,
            dislikes = 0
        )
    )
}

data class Comment(
    val userProfileUrl: String,
    val userName: String,
    val content: String,
    val rating: Int,
    val likes: Int,
    val dislikes: Int
)
