package com.example.projettdm

import android.content.Context
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
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.projettdm.ui.theme.CustomTypography
import kotlinx.coroutines.delay

@Composable
fun FoodifyGoApp(navHostController: NavHostController) {

    LaunchedEffect(Unit) {
        delay(2000)
        navHostController.navigate("acceuil2")
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF56949))
            .padding(16.dp)
            .clickable {
                navHostController.navigate("acceuil2") // Navigate on click
            },
        contentAlignment = Alignment.TopCenter // Align content at the top
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .background(Color(0xFFF56949)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Image(
                painter = painterResource(id = R.drawable.icon_location2), // Replace with your image
                contentDescription = "Logo",
                modifier = Modifier
                    .size(200.dp)
                    .background(Color(0xFFF56949)) // Set background color
            )
            Text(
                text = "FoodifyGo",
                style = CustomTypography.headlineLarge,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(200.dp))

            CircularProgressIndicator(
                color = Color.Black,
                strokeWidth = 5.dp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
@Composable
fun Acceuil2(navHostController: NavHostController, context: Context) {
    val poppinsBold = FontFamily(
        Font(R.font.poppins_bold),
    )
    val poppinsSemiBold = FontFamily(
        Font(R.font.poppins_semibold)
    )

    // Récupérer la valeur de isFirstLaunch depuis SharedPreferences
    val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)

    // Automatiquement naviguer après 2 secondes
    LaunchedEffect(Unit) {
        delay(2000)  // Délai de 2 secondes

        // Si c'est le premier lancement, on navigue vers "create_account"
        if (isFirstLaunch) {
            navHostController.navigate("pages") {
                // On peut éventuellement vider la pile de navigation précédente
                popUpTo("acceuil2") { inclusive = true }
            }

            // Changer la valeur de isFirstLaunch à false après la première navigation
            sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
        } else {
            // Sinon, on navigue vers "pages"
            navHostController.navigate("create_account") {
                popUpTo("acceuil2") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFA726))
            .clickable {
                // Naviguer vers "pages" sur un clic
                if (isFirstLaunch) {
                    navHostController.navigate("pages")
                } else {
                    navHostController.navigate("create_account")
                }
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.pizza),
            contentDescription = "Pizza Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // Align vers le haut
        ) {
            // Ajuster l'espacement du texte en laissant un peu d'espace en haut
            Spacer(modifier = Modifier.height(240.dp)) // Ajustez cette valeur pour le margin

            // Texte "Welcome to"
            Text(
                text = "Welcome to",
                fontFamily = poppinsBold,
                fontSize = 34.sp,
                color = Color.White // Texte blanc
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Texte "FoodifyGo" en orange avec Poppins Bold
            Text(
                text = "FoodifyGo",
                fontFamily = poppinsBold,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF56949)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Texte supplémentaire avec Poppins Semi-Bold
            Text(
                text = "The fastest food delivery service.",
                fontFamily = poppinsSemiBold,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White // Texte blanc
            )
        }
    }
}


@Composable
fun PageScreen(navHostController: NavHostController) {
    // Les données des pages
    val images = listOf(R.drawable.image1, R.drawable.image2, R.drawable.image3)
    val titles = listOf("Search Restaurants", "Discover New Places", "Enjoy Your Meals")
    val descriptions = listOf(
        "In publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document or.",
        "Explore various locations and discover amazing restaurants around the globe.",
        "Relish delicious meals in your favorite spots with your loved ones."
    )

    // État pour suivre la page active
    var currentPage by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable {
                // Incrémenter la page active tout en s'assurant de ne pas dépasser la page 3
                if (currentPage <= 2) {
                    currentPage += 1
                }
            }
    ) {
        Spacer(modifier=Modifier.height(20.dp))
        Image(
            painter = painterResource(id = images[currentPage]),
            contentDescription = "Page Image",
            contentScale = ContentScale.Fit, // Garde l'image dans les limites sans la déformer
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
                .height(300.dp)
        )

        Spacer(modifier=Modifier.height(40.dp))
        Text(
            text = titles[currentPage],
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            color = Color(0xFFF56949),
            style = CustomTypography.labelSmall,
            fontSize = 22.sp
        )

        Text(
            text = descriptions[currentPage],
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            color = Color.Black,
            style = CustomTypography.displaySmall,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            images.indices.forEach { index ->
                PaginationDot(
                    isSelected = index == currentPage,
                    onClick = { currentPage = index }
                )
                if (index < images.size - 1) {
                    Spacer(modifier = Modifier.width(12.dp)) // Espace entre les cercles
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        if (currentPage == 2) {
            Button(
                onClick = {
                    navHostController
                        .navigate("create_account") // Navigate to create_account
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF56949)
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Get started", style= CustomTypography.labelSmall, color=Color.Black)
            }
        }
    }
}


@Composable
fun PaginationDot(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .border(width = 1.dp, Color.Black)
            .background(if (isSelected) Color.Black else Color.White)
            .padding(8.dp)
            .clickable { onClick() }
    )
}
