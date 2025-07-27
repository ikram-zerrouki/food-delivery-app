package com.example.projettdm.ui.theme
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.projettdm.R
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Define custom font families
val PoppinsBold = FontFamily(
    Font(R.font.poppins_bold) // Add your bold font here
)

val PoppinsSemiBold = FontFamily(
    Font(R.font.poppins_semibold) // Add your semibold font here
)

val PoppinsRegular = FontFamily(
    Font(R.font.poppins_regular)
)

val RobotoMedium = FontFamily(
    Font(R.font.roboto_medium)
)

val CustomTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = PoppinsBold,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = PoppinsSemiBold,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = PoppinsSemiBold,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodySmall = TextStyle(
        fontFamily = PoppinsRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = RobotoMedium,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    displaySmall = TextStyle(
        fontFamily = RobotoMedium,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp

    )

)
