package com.example.projettdm

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint.Align
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projettdm.ui.theme.CustomTypography
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait
import org.bson.Document
import org.bson.types.Binary
import org.bson.types.ObjectId
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.io.ByteArrayInputStream
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

fun fetchRestaurants(callback: (List<Pair<String, ByteArray>>) -> Unit) {
    // Create a single-threaded executor to handle asynchronous tasks
    val executor = Executors.newSingleThreadExecutor()
    val collection = getUsersCollection("Restaurant")
    executor.execute {
        try {
            val future = CompletableFuture<List<Pair<String, ByteArray>>>()
            collection.find().subscribe(object : Subscriber<Document> {
                private val restaurants = mutableListOf<Pair<String, ByteArray>>()

                override fun onSubscribe(subscription: Subscription) {
                    subscription.request(Long.MAX_VALUE) // Request all documents
                }

                override fun onNext(document: Document) {
                    val name = document.getString("Name")
                    val imageBinary = document.get("Image") as? Binary
                    val imageData = imageBinary?.data // Get byte array from Binary data
                    if (name != null && imageData != null) {
                        restaurants.add(Pair(name, imageData)) // Add as Pair<String, ByteArray>
                    }
                }

                override fun onError(throwable: Throwable) {
                    future.completeExceptionally(throwable)
                }

                override fun onComplete() {
                    future.complete(restaurants)
                }
            })

            // Wait for the result and invoke the callback
            callback(future.get())
        } catch (e: Exception) {
            e.printStackTrace()
            callback(emptyList()) // Return an empty list in case of error
        } finally {
            executor.shutdown() // Shut down the executor
        }
    }
}
data class PopularItemData(
    val _id : ObjectId,
    val name: String,
    val rating: Double,
    val reviewCount: Int,
    val imageBitmap: ImageBitmap // Store ImageBitmap here
)

fun fetchPopularMenuItems(callback: (List<PopularItemData>) -> Unit) {
    val executor = Executors.newSingleThreadExecutor()
    val menuItemCollection = getUsersCollection("menu_item")

    executor.execute {
        try {
            val future = CompletableFuture<List<PopularItemData>>()
            menuItemCollection.find()
                .sort(Sorts.descending("Rating")) // Sort by Rating (highest first)
                .limit(10) // Fetch only the top 10 items
                .subscribe(object : Subscriber<Document> {
                    private val popularItems = mutableListOf<PopularItemData>()

                override fun onSubscribe(subscription: Subscription) {
                    subscription.request(Long.MAX_VALUE) // Request all documents
                }

                override fun onNext(document: Document) {
                    val _id = document.getObjectId("_id") ?: ObjectId("Unknown")
                    val name = document.getString("Name") ?: "Unknown"
                    val rating = document.getDouble("Rating") ?: 0.0
                    val reviewCount = document.getInteger("reviewCount") ?: 0
                    val imageBinary = document.get("Photo") as? Binary
                    val imageData = imageBinary?.data // Get byte array from Binary data

                    // Convert ByteArray to ImageBitmap
                    val imageBitmap = imageData?.let {
                        BitmapFactory.decodeByteArray(it, 0, it.size).asImageBitmap()
                    }

                    if (imageBitmap != null) {
                        popularItems.add(PopularItemData(_id,name, rating, reviewCount, imageBitmap))
                    }
                }

                override fun onError(throwable: Throwable) {
                    future.completeExceptionally(throwable)
                }

                override fun onComplete() {
                    future.complete(popularItems)
                }
            })

            // Wait for the result and invoke the callback
            callback(future.get())
        } catch (e: Exception) {
            e.printStackTrace()
            callback(emptyList()) // Return an empty list in case of error
        } finally {
            executor.shutdown() // Shut down the executor
        }
    }
}

@Composable
fun PopularItem(userId: String,items: List<PopularItemData>) {
    val context = LocalContext.current
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)

    ) {
        items(items.size) { index ->
            Column(
                modifier = Modifier
                    .padding(end = 8.dp)  // Adds padding to the right
                    .background(Color.Transparent),
            ) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .background(Color.Transparent, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.TopStart  // Align content inside Box to bottom-left
                ) {
                    Image(
                        bitmap = items[index].imageBitmap, // Use the ImageBitmap here
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.Start, // Align all text to the left
                    ) {
                        // Name at the top left
                        Text(
                            text = items[index].name,
                            color = Color.White,
                            style = CustomTypography.headlineLarge,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start  // Align the name text to the left
                        )

                        Spacer(modifier = Modifier.height(70.dp))

                        // Rating and review count aligned to the left
                        Text(
                            text = "${items[index].rating} (${items[index].reviewCount})",
                            color = Color.White,
                            style = CustomTypography.bodyMedium,
                            fontSize = 10.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center // Align rating and review count to the left
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Button aligned to the left
                        Button(
                            onClick = { addToCart(userId,items[index]._id,context) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF56949)),
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(text = "ADD TO CART", style = CustomTypography.labelSmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

fun fetchMenuItems(userId: String, onItemsFetched: (List<Pair<String, Bitmap>>) -> Unit) {
    val orderCollection = getUsersCollection("Order") // Get the "Order" collection
    val menuItemCollection = getUsersCollection("menu_item") // Get the "menu_item" collection

    runBlocking {
        try {
            val filter = Filters.eq("Utilisateur", userId)
            val userOrder = orderCollection.find(filter).awaitFirstOrNull()

            if (userOrder != null) {
                // Get the list of menu item IDs from the "Menu_items" field
                val menuItemIds = userOrder["Menu_items"] as? List<String> ?: emptyList()
                val menuItemsWithImages = menuItemIds.mapNotNull { itemId ->
                    val menuItem = menuItemCollection.find(Filters.eq("_id", ObjectId(itemId))).awaitFirstOrNull()
                    println(menuItem)
                    if (menuItem != null) {
                        // Debug: Log item details
                        println("Fetched menu item: ${menuItem["Name"]}")

                        val itemName = menuItem["Name"] as? String ?: return@mapNotNull null
                        val binaryImage: Binary = menuItem["Photo"] as? Binary ?: return@mapNotNull null
                        val byteArray = binaryImage.data
                        val imageBitmap = binaryToBitmap(byteArray)
                        if (imageBitmap != null) {
                            Pair(itemName, imageBitmap)
                        } else {
                            null
                        }
                    } else {
                        println("Menu item with ID $itemId not found")
                        null
                    }
                }
                println("menu items with images")
                println(menuItemsWithImages)
                onItemsFetched(menuItemsWithImages)
            } else {
                // Debug: Log if userOrder is null
                println("No user order found for userId: $userId")
            }
        } catch (e: Exception) {
            // Print full exception details for debugging
            println("Error during fetching menu items: ${e.message}")
            e.printStackTrace()
        }
    }
}



@Composable
fun HomeScreen(navController: NavController, userId: String) {

    var restaurantItems by remember { mutableStateOf<List<Pair<String, ByteArray>>>(emptyList()) }
    var typeItems by remember { mutableStateOf<List<Pair<String, ByteArray>>>(emptyList()) }
    var specialOffers by remember { mutableStateOf<List<Pair<String, ByteArray>>>(emptyList()) }
    var refreshKey by remember { mutableStateOf(0) }
    var popularItems by remember { mutableStateOf<List<PopularItemData>>(emptyList()) }
    var previousMenuItems by remember { mutableStateOf<List<Pair<String, Bitmap>>>(emptyList()) }

    LaunchedEffect(refreshKey) {
        fetchRestaurants { fetchedItems -> restaurantItems = fetchedItems }
        fetchTypes { fetchedTypes -> typeItems = fetchedTypes }
        fetchSpecialOffers { fetchedOffers -> specialOffers = fetchedOffers }
        if (userId.isNotEmpty()) {
            fetchMenuItems(userId) { items ->
                previousMenuItems = items
                println(previousMenuItems)
            }
        }
        fetchPopularMenuItems { fetchedItems ->
            popularItems = fetchedItems
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
    ) {
        SearchBar(onRefresh = { refreshKey++ })

        // Order Again Section
        SectionTitle(navController, "orderHistory/$userId", "Order Again")
        Spacer(modifier = Modifier.height(8.dp))
        if (previousMenuItems.isNotEmpty()) {
            OrderAgain(menuItems = previousMenuItems)
        }

        Spacer(modifier = Modifier.height(8.dp))
        SectionTitle(navController, "", "Special Offers")

        // Show Special Offers
        if (specialOffers.isNotEmpty()) {
            specialOffers.forEach { (menuItem, imageData) ->
                val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size) // Convert ByteArray to Bitmap
                SpecialOfferBanner(menuItem = menuItem, imageRes = bitmap)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Categories",
            style = CustomTypography.titleMedium,
            modifier = Modifier
                .fillMaxWidth() // Optional: Ensures the text spans the full width
                .padding(horizontal = 16.dp, vertical = 8.dp) // Adjust the padding as needed
        )
        CategoryGrid()
        Spacer(modifier = Modifier.height(25.dp))
        SectionTitle(navController, "", "Popular Items")
        Spacer(modifier = Modifier.height(8.dp))
        if (popularItems.isNotEmpty()) {
            PopularItem(userId,items = popularItems)
        }
        Spacer(modifier = Modifier.height(8.dp))
        SectionTitle(navController, "", "Restaurants near you")
        if (restaurantItems.isNotEmpty()) {
            RestaurantRow(items = restaurantItems)
        }

        Spacer(modifier = Modifier.height(18.dp))
        BottomNavBar()
    }
}
@Composable
fun SearchBar(onRefresh: () -> Unit) {
    var textValue = remember { mutableStateOf(TextFieldValue("")) }
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFFFFFFFF), shape = RoundedCornerShape(16.dp)) // Arrondir les coins
                .weight(4f) // Augmente le poids pour élargir le champ texte
                .padding(4.dp) // Contrôle l'espace intérieur
                .border(1.5.dp, Color.Gray, RoundedCornerShape(50.dp)), // Bordures
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp) // Espace autour du contenu
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_loupe),
                    contentDescription = "Search Icon",
                    modifier = Modifier
                        .size(28.dp) // Taille de l'icône
                        .padding(end = 8.dp), // Espace entre l'icône et le texte
                    tint = Color.Unspecified
                )

                BasicTextField(
                    value = textValue.value,
                    onValueChange = { newValue -> textValue.value = newValue },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (textValue.value.text.isEmpty()) {
                            Text(
                                text = "Search",
                                style = CustomTypography.labelSmall,
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }
        IconButton(onClick = { onRefresh() }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_refresh),
                contentDescription = "Refresh",
                modifier = Modifier.size(28.dp),
                tint = Color.Unspecified
            )
        }
    }
}


@Composable
fun SectionTitle(navController: NavController, next: String, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = CustomTypography.titleMedium)

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ALL",
                style = CustomTypography.labelSmall,
                modifier = Modifier.clickable {
                    // Navigate to the order_history screen when clicked
                    navController.navigate(next)
                }
            )

            // Spacer between "ALL" and the icon
            Spacer(modifier = Modifier.width(4.dp))

            // Custom Icon
            Icon(
                painter = painterResource(id = R.drawable.forward_arrow), // Replace with your own icon's resource name
                contentDescription = "Arrow",
                modifier = Modifier.size(18.dp), // Adjust the size of the icon if needed
                tint = Color.Unspecified
            )
        }
    }
}



@Composable
fun ItemRow(items: List<Pair<String, Int>>) { // List of Pair<Title, ImageResource>
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        items(items.size) { index ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(end = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.LightGray, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = items[index].second),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = items[index].first,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun OrderAgain(menuItems: List<Pair<String, Bitmap>>) { // List of Pair<Title, Bitmap>
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        items(menuItems.size) { index ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(end = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.LightGray, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Convert Bitmap to ImageBitmap
                    val imageBitmap = menuItems[index].second.asImageBitmap()

                    Image(
                        bitmap = imageBitmap,  // Use ImageBitmap instead of Bitmap
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = menuItems[index].first,
                    textAlign = TextAlign.Center,
                    style = CustomTypography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


@Composable
fun SpecialOfferBanner(menuItem: String, imageRes: Bitmap) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFFFFFFF), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = imageRes.asImageBitmap(), // Convert Bitmap to ImageBitmap
            contentDescription = menuItem,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

fun fetchSpecialOffers(callback: (List<Pair<String, ByteArray>>) -> Unit) {
    // Create a single-threaded executor to handle asynchronous tasks
    val executor = Executors.newSingleThreadExecutor()
    val collection = getUsersCollection("Special Offers") // Replace with your MongoDB collection access
    executor.execute {
        try {
            val future = CompletableFuture<List<Pair<String, ByteArray>>>()
            collection.find().subscribe(object : Subscriber<Document> {
                private val specialOffers = mutableListOf<Pair<String, ByteArray>>()

                override fun onSubscribe(subscription: Subscription) {
                    subscription.request(Long.MAX_VALUE) // Request all documents
                }

                override fun onNext(document: Document) {
                    val menuItem = document.getString("Menu_item") // Retrieve Menu Item
                    val imageBinary = document.get("image") as? Binary
                    val imageData = imageBinary?.data // Get byte array from Binary data
                    if (menuItem != null && imageData != null) {
                        specialOffers.add(Pair(menuItem, imageData)) // Add as Pair<String, ByteArray>
                    }
                }

                override fun onError(throwable: Throwable) {
                    future.completeExceptionally(throwable)
                }

                override fun onComplete() {
                    future.complete(specialOffers)
                }
            })

            // Wait for the result and invoke the callback
            callback(future.get())
        } catch (e: Exception) {
            e.printStackTrace()
            callback(emptyList()) // Return an empty list in case of error
        } finally {
            executor.shutdown() // Shut down the executor
        }
    }
}

fun fetchTypes(callback: (List<Pair<String, ByteArray>>) -> Unit) {
    val executor = Executors.newSingleThreadExecutor()
    val collection = getUsersCollection("Type")
    executor.execute {
        try {
            val future = CompletableFuture<List<Pair<String, ByteArray>>>()
            collection.find().subscribe(object : Subscriber<Document> {
                private val types = mutableListOf<Pair<String, ByteArray>>()

                override fun onSubscribe(subscription: Subscription) {
                    subscription.request(Long.MAX_VALUE) // Request all documents
                }

                override fun onNext(document: Document) {
                    val name = document.getString("name")
                    val imageBinary = document.get("image") as? Binary
                    val imageData = imageBinary?.data // Get byte array from Binary data
                    if (name != null && imageData != null) {
                        types.add(Pair(name, imageData))
                    }
                }

                override fun onError(throwable: Throwable) {
                    future.completeExceptionally(throwable)
                }

                override fun onComplete() {
                    future.complete(types)
                }
            })

            callback(future.get())
        } catch (e: Exception) {
            e.printStackTrace()
            callback(emptyList()) // Return an empty list in case of error
        } finally {
            executor.shutdown() // Shut down the executor
        }
    }
}


@Composable
fun CategoryGrid() {
    var types by remember { mutableStateOf<List<Pair<String, ByteArray>>>(emptyList()) }

    LaunchedEffect(Unit) {
        fetchTypes { result ->
            types = result
        }
    }

    if (types.isEmpty()) {
        Text("Loading...", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    } else {
        val categories = types.map { it.first }
        val images = types.mapNotNull { binaryToBitmap(it.second) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            for (i in categories.indices step 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val endIndex = (i + 3).coerceAtMost(categories.size)
                    val subList = categories.subList(i, endIndex)

                    subList.forEachIndexed { index, category ->
                        if (i + index < images.size) {
                            CategoryCard(category = category, imageRes = images[i + index])
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CategoryCard(category: String, imageRes: Bitmap) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .width(100.dp), // Set width instead of size, so the height is flexible
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp), // Fixed height for image container
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = imageRes.asImageBitmap(),  // Use asImageBitmap() to convert Bitmap to ImageBitmap
                contentDescription = category,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)), // Optional: add rounded corners for the image
                contentScale = ContentScale.Crop
            )
        }

        // Display category name directly below the image
        Text(
            text = category,
            textAlign = TextAlign.Center,
            style = CustomTypography.bodyMedium,
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp) // Small padding to separate text from the image
        )
    }
}



fun binaryToBitmap(binary: ByteArray): Bitmap? {
    return try {
        val inputStream = ByteArrayInputStream(binary)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun RestaurantRow(items: List<Pair<String, ByteArray>>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        items(items.size) { index ->
            val (name, imageBinary) = items[index]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(end = 8.dp)
            ) {
                val bitmap = binaryToBitmap(imageBinary) // Convert Binary to Bitmap

                // Display image if bitmap is available
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = name,
                        modifier = Modifier
                            .size(120.dp) // Ensure the image fits the 120.dp box
                            .background(Color.White), // Placeholder color
                    )
                } ?: run {
                    // Fallback image when binary is null or not available
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .background(Color.White) // Placeholder color
                    ) {
                        Text(
                            text = "No Image",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
                Text(
                    text = name,
                    textAlign = TextAlign.Center,
                    style = CustomTypography.bodyMedium,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp) // Small padding to separate text from the image
                )
                Button(
                    onClick = {
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF56949)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("See Menu")
                }
            }
        }
    }
}



@Composable
fun BottomNavBar() {
    val items = listOf("Home", "Search", "Order", "Profile")
    var selectedIndex by remember { mutableStateOf(0) }

    BottomNavigation(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEFF3FC)),
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

