package com.example.projettdm

import android.content.Context
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CartItemData(
    val id: Int,
    val imageRes: Int,
    val title: String,
    val pricePerUnit: Double,
    var quantity: Int
)

@Composable
fun FullPanierScreen(context: Context) {
    var cartItems by remember {
        mutableStateOf(
            listOf(
                CartItemData(
                    id = 1,
                    imageRes = R.drawable.pasta_image,
                    title = "Cheese Hot Pasta",
                    pricePerUnit = 750.0,
                    quantity = 2
                ),
                CartItemData(
                    id = 2,
                    imageRes = R.drawable.pizza_image,
                    title = "Cheese Hot Pizza",
                    pricePerUnit = 500.0,
                    quantity = 3
                )
            )
        )
    }

    val deliveryFee = 450.0
    val subTotal = cartItems.sumOf { it.pricePerUnit * it.quantity }
    val total = subTotal + deliveryFee

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back Arrow",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterStart)
                        .clickable {
                            // Handle back navigation
                        }
                )

                Text(
                    text = "My Cart",
                    style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Cart Items Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                cartItems.forEach { item ->
                    CartItem(
                        cartItem = item,
                        onQuantityChange = { id, newQuantity ->
                            cartItems = cartItems.map {
                                if (it.id == id) it.copy(quantity = newQuantity) else it
                            }
                        },
                        onDelete = { id ->
                            cartItems = cartItems.filter { it.id != id }
                        }
                    )
                }

                // Add more items link
                Text(
                    text = "+ Add more items",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                    color = Color(0xFFE8581C),
                    modifier = Modifier.clickable {
                        // Handle add more items action
                    }
                )
            }

            // Price Details Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PriceDetailRow(label = "Sub total", price = String.format("%.2f DZD", subTotal))
                PriceDetailRow(label = "Delivery Fee", price = String.format("%.2f DZD", deliveryFee))
                PriceDetailRow(
                    label = "Total",
                    price = String.format("%.2f DZD", total),
                    isTotal = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        // Handle checkout action
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE8581C)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "CHECKOUT",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }

        // Bottom navigation bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            CartBottomNavBar()
        }
    }
}

@Composable
fun CartItem(
    cartItem: CartItemData,
    onQuantityChange: (id: Int, newQuantity: Int) -> Unit,
    onDelete: (id: Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = cartItem.imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .padding(end = 16.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = cartItem.title,
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Text(
                text = String.format("%.2f DZD", cartItem.pricePerUnit),
                style = TextStyle(fontSize = 16.sp),
                color = Color.Gray
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (cartItem.quantity > 1) onQuantityChange(cartItem.id, cartItem.quantity - 1)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_removep1),
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
                Text(
                    text = "${cartItem.quantity}",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                    color = Color.Black
                )
                IconButton(onClick = {
                    onQuantityChange(cartItem.id, cartItem.quantity + 1)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_addp1),
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            }

            Text(
                text = "Delete",
                style = TextStyle(fontSize = 14.sp, color = Color.Red),
                modifier = Modifier.clickable { onDelete(cartItem.id) }
            )
        }
    }
}

@Composable
fun PriceDetailRow(label: String, price: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = if (isTotal) 18.sp else 16.sp,
                fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium
            ),
            color = Color.Black
        )
        Text(
            text = price,
            style = TextStyle(
                fontSize = if (isTotal) 18.sp else 16.sp,
                fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium
            ),
            color = Color.Black
        )
    }
}
