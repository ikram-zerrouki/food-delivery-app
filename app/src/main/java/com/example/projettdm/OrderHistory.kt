package com.example.projettdm

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.projettdm.ui.theme.CustomTypography
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.collect
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.types.Binary
import org.bson.types.ObjectId

@Composable
fun OrderHistoryScreen(navController: NavController, userId: String) {
    // State to hold the list of orders
    val orderHistoryList = remember { mutableStateOf<List<OrderWithMenuItems>>(emptyList()) }

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { BottomNavBar() }, // Assuming you already have BottomNavBar
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Test the fetching function by logging the result
                LaunchedEffect(Unit) {
                    fetchOrdersWithMenuItems(userId) { orders ->
                        // Update the state with the fetched orders
                        orderHistoryList.value = orders
                    }
                }

                // Check if orders have been fetched and display accordingly
                if (orderHistoryList.value.isEmpty()) {
                    Text(
                        text = "Fetching data for user $userId...",
                        style = CustomTypography.labelSmall
                    )
                } else {
                    // Display the order history list once data is fetched
                    OrderHistoryList(userId,orderHistoryList.value)
                }
            }
        }
    )
}


@Composable
fun OrderHistoryList(userId: String,orderHistoryList: List<OrderWithMenuItems>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(orderHistoryList) { order -> // Iterate over each order
            OrderHistoryItem(userId,order) // Display each order using OrderHistoryItem
        }
    }
}
@Composable
fun OrderHistoryItem(userId : String, order: OrderWithMenuItems) {
    val context = LocalContext.current
    order.menuItems.forEach { menuItem ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = MaterialTheme.shapes.small,
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)

            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = menuItem.name,
                        style = CustomTypography.bodyMedium,
                        fontSize =20.sp,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                // Image of the menu item
                menuItem.photo.let { imageRes ->
                    Image(
                        bitmap = imageRes.asImageBitmap(),
                        contentDescription = menuItem.name,
                        modifier = Modifier
                            .width(240.dp)  // Réduire la largeur à 150dp
                            .height(150.dp) // Garder la hauteur de 100dp (ou ajustez selon votre préférence)
                            .align(Alignment.CenterHorizontally),


                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${menuItem.restaurantName}",
                    style = CustomTypography.titleMedium,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Price: ${menuItem.price} DZD",
                    style = CustomTypography.displaySmall,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                // Order date and status
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = order.date,
                    style = CustomTypography.displaySmall,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,  // Align horizontally in the center
                    verticalAlignment = Alignment.CenterVertically // Align vertically in the center
                ) {

                    Text(
                        text = order.etat,
                        style = CustomTypography.displaySmall,
                        fontSize = 14.sp,
                        color = if (order.etat == "Delivered") Color(0xFF35B82A) else Color.Red,
                        modifier = Modifier.padding(start = 8.dp) // Add space between icon and text
                    )
                    // Add the condition to display the appropriate icon
                    if (order.etat == "Delivered" ) {
                        Icon(
                            imageVector = Icons.Default.Check,  // Icon for "Delivered" (green)
                            contentDescription = "Delivered",
                            tint = Color(0xFF35B82A)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Close,  // Icon for non-delivered status (red)
                            contentDescription = "Not Delivered",
                            tint = Color.Red
                        )
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                // Repeat Order Button
                Button(
                    onClick = { addToCart(userId,menuItem._id,context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF36F56)),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = "REPEAT ORDER",
                        style = CustomTypography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

fun addToCart(userId: String, menuItemId: ObjectId, context: Context) {
    val cartCollection = getUsersCollection("Panier") // Récupère la collection "Carts"

    runBlocking {
        try {
            // Vérifie si un document existe avec l'utilisateur donné
            val existingCart = cartCollection.find(Document("Utilisateur", userId)).awaitFirstOrNull()

            if (existingCart != null) {
                val updateResult = cartCollection.updateOne(
                    Filters.eq("Utilisateur", userId), // Filtre pour trouver l'utilisateur
                    Updates.addToSet("Items", menuItemId.toString()) // Mise à jour pour ajouter l'item
                ).awaitFirstOrNull()

                if ((updateResult?.modifiedCount ?: 0) > 0) {
                    Toast.makeText(context, "Item added to cart", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Item already in cart", Toast.LENGTH_SHORT).show()
                }
            } else {
                val newCart = Document().apply {
                    append("Utilisateur", userId)
                    append("Items", listOf(menuItemId.toString()))
                }

                val insertResult = cartCollection.insertOne(newCart).awaitFirstOrNull()
                if (insertResult != null) {
                    Toast.makeText(context, "Cart created and item added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to create cart", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            // Gère les erreurs
            println("Error updating cart: ${e.message}")
            Toast.makeText(context, "Error updating cart", Toast.LENGTH_SHORT).show()
        }
    }
}



@Composable
fun TopBar(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp), // Padding to move it closer to the top
    ) {
        // Back Arrow (Top-Left)
        androidx.compose.material3.IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(start = 16.dp)
                .size(24.dp) // Align to top-left and smaller size
        ) {
            androidx.compose.material3.Icon(
                painter = painterResource(id = R.drawable.back), // Replace with your back arrow icon
                contentDescription = "Back",
                tint = Color.Unspecified
            )
        }

        // Title (Centered)
        androidx.compose.material3.Text(
            text = "Order History",
            style = CustomTypography.labelSmall,
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center) // Align to center of the Box
        )
    }
}

fun fetchOrdersWithMenuItems(userId: String, onOrdersFetched: (List<OrderWithMenuItems>) -> Unit) {
    val orderCollection = getUsersCollection("Order") // "Order" collection
    val menuItemCollection = getUsersCollection("menu_item") // "menu_item" collection

    runBlocking {
        try {
            // Fetch all orders for the user
            val filter = Filters.eq("Utilisateur", userId) // Filter to match the current user
            val userOrders = orderCollection.find(filter).asFlow() // Convert query to a flow

            // List to store the result
            val ordersWithMenuItems = mutableListOf<OrderWithMenuItems>()

            // Collect and process orders
            userOrders.collect { userOrder ->
                val etat = userOrder["Etat"] as? String ?: "Unknown"
                val date = userOrder["Date"] as? String ?: "Unknown"
                val menuItemIds = userOrder["Menu_items"] as? List<String> ?: emptyList()

                // Fetch menu item details for this order
                val menuItems = menuItemIds.mapNotNull { itemId ->
                    val menuItem = menuItemCollection.find(Filters.eq("_id", ObjectId(itemId))).awaitFirstOrNull()

                    if (menuItem != null) {
                        val _id = menuItem["_id"] as? ObjectId ?: return@mapNotNull null
                        val itemName = menuItem["Name"] as? String ?: return@mapNotNull null
                        val price = menuItem["Price"] as? Double ?: return@mapNotNull null
                        val binaryImage = menuItem["Photo"] as? Binary ?: return@mapNotNull null
                        val restaurantName = menuItem["Restaurant name"] as? String ?: return@mapNotNull null
                        val byteArray = binaryImage.data
                        val imageBitmap = binaryToBitmap(byteArray)

                        if (imageBitmap != null) {
                            MenuItemDetails(_id, itemName, price, imageBitmap, restaurantName)
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                }

                // Add to result list
                ordersWithMenuItems.add(OrderWithMenuItems(etat, date, menuItems))
            }

            // Return the fetched orders with menu items
            onOrdersFetched(ordersWithMenuItems)
        } catch (e: Exception) {
            println("Error during fetching orders and menu items: ${e.message}")
            e.printStackTrace()
            onOrdersFetched(emptyList()) // Return empty list in case of an error
        }
    }
}


// Helper classes to structure the fetched data
data class OrderWithMenuItems(
    val etat: String,
    val date: String,
    val menuItems: List<MenuItemDetails>
)

data class MenuItemDetails(
    val _id: ObjectId,
    val name: String,
    val price: Double,
    val photo: Bitmap,
    val restaurantName: String
)



//@Composable
//fun OrderItemsList(items: List<OrderItem>) {
//    Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
//        items.forEach { item ->
//            Text(
//                text = "${item.name} x ${item.quantity} - ${item.price} DZD",
//                fontSize = 12.sp,
//                color = Color.Gray
//            )
//        }
//    }
//}

// Data model for Order
data class Order(
    val restaurantName: String,
    val date: String,
    val status: String,
    val totalPrice: Double,
    val items: List<OrderItem>,
    val imageRes: Int
)

// Data model for OrderItem
data class OrderItem(
    val name: String,
    val quantity: Int,
    val price: Double
)


