package com.example.projettdm

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projettdm.ui.theme.CustomTypography
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClients
import org.bson.Document


@Composable
fun CreateAccountScreen(navController: NavController) {
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val authService = AuthService()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color = Color(0xFFFFFFFF)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Icon
        Image(
            painter = painterResource(id = R.drawable.icon_location),
            contentDescription = "Location Icon",
            modifier = Modifier
                .size(200.dp)
        )
        Text(
            text = "Create New Account",
            style = CustomTypography.headlineLarge,
            fontSize = 28.sp,
            color = Color.Black,
        )

        Spacer(modifier = Modifier.height(5.dp)) // Espacement minimal avant les champs de texte

        // Name Field
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name", style = CustomTypography.displaySmall) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email",style = CustomTypography.displaySmall) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password",style = CustomTypography.displaySmall) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible) R.drawable.seen else R.drawable.close_eye
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Confirm Password Field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password",style = CustomTypography.displaySmall) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (confirmPasswordVisible) R.drawable.seen else R.drawable.close_eye
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                // Check if any field is empty
                if (name.text.isEmpty() || email.text.isEmpty() || password.text.isEmpty() || confirmPassword.text.isEmpty()) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                } else if (password.text != confirmPassword.text) {
                    // Check if passwords match
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                } else {
                    authService.register(name.text,email.text,password.text,context,navController)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFF56949)),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(text = "SIGN UP", color = Color.White, style = CustomTypography.labelSmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Already have an account
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Already have an account? ", style = CustomTypography.displaySmall,
                fontSize = 14.sp)
            Text(
                text = "Sign in.",
                style = CustomTypography.displaySmall,
                fontSize = 14.sp,
                color = Color(0xFFF56949),
                modifier = Modifier.clickable {
                    navController.navigate("login")
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {
                // Handle Facebook Login
            }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_facebook),
                    contentDescription = "Facebook Login",
                    modifier = Modifier.size(50.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick ={}
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google Login",
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}


@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val authService = AuthService()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color = Color(0xFFFFFFFF)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.rocket_image),
            contentDescription = "Rocket illustration",
            modifier = Modifier.size(280.dp)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Let's get you started",
            style = CustomTypography.headlineLarge,
            fontSize = 28.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email",style = CustomTypography.displaySmall) },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password",style = CustomTypography.displaySmall) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible }
                ) {
                    if (passwordVisible) {
                        Icon(
                            painter = painterResource(id = R.drawable.seen),
                            contentDescription = "Hide password",
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.close_eye),
                            contentDescription = "Show password",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Forgot password?",
            color = Color(0xFFF56949),
            modifier = Modifier
                .align(Alignment.End)
                .clickable {
                    navController.navigate("forgot_password")
                },
            style = CustomTypography.displaySmall,
            fontSize = 14.sp

        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (email.text.isEmpty() || password.text.isEmpty()) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                } else {
                    authService.signIn(email.text, password.text, context, navController)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFF56949)),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(text = "SIGN IN", color = Color.White, style = CustomTypography.labelSmall)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account? ",
                style = CustomTypography.displaySmall,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "Sign up.",
                style = CustomTypography.displaySmall,
                color = Color(0xFFF56949),
                fontSize = 14.sp,
                modifier = Modifier.clickable {
                    navController.navigate("create_account")
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {
                // Handle Facebook Login
            }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_facebook),
                    contentDescription = "Facebook Login",
                    modifier = Modifier.size(50.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = {}
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google Login",
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val email = remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar: Back Icon and Centered Title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp), // Padding to move it closer to the top
        ) {
            // Back Arrow (Top-Left)
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart).size(24.dp) // Align to top-left and smaller size
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back), // Replace with your back arrow icon
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }

            // Title (Centered)
            Text(
                text = "Forgot Password",
                style = CustomTypography.labelSmall,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center) // Align to center of the Box
            )
        }

        Spacer(modifier = Modifier.height(32.dp)) // Space after header

        // Instructions Text
        Text(
            text = "Please enter your email address. You will receive a link to create a new password via email.",
            style = CustomTypography.displaySmall,
            fontSize = 15.sp,
            color = Color(0xFF80869A),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(35.dp))

        // Email Field
        OutlinedTextField(
            value = email.value,  // Using TextFieldValue
            onValueChange = { email.value = it },
            label = { Text("Email", style = CustomTypography.displaySmall) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(40.dp),
            singleLine = true,
            shape = RoundedCornerShape(30.dp),
        )

        Spacer(modifier = Modifier.height(50.dp))

        // Send Button
        Button(
            onClick = {
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(Color(0xFFF56949)) // Orange color
        ) {
            Text(text = "SEND", color = Color.White, style = CustomTypography.labelSmall)
        }
    }
}

fun getUsersCollection(collectionName : String): com.mongodb.reactivestreams.client.MongoCollection<Document> {
    val connectionString = ConnectionString("mongodb://192.168.43.166:27017/?directConnection=true&serverSelectionTimeoutMS=40000")
    val settings = MongoClientSettings.builder()
        .applyConnectionString(connectionString)
        .build()
    val client = MongoClients.create(settings)
    val database = client.getDatabase("myDatabase")
    return database.getCollection(collectionName)
}




fun GoogleLogin(context: Context) {
    val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("811702987948-nf7qqhvfolkqrheuef2qvn4dm1mnkr8m.apps.googleusercontent.com") // Replace with your Web Client ID
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
    val signInIntent = googleSignInClient.signInIntent

    // This requires starting the sign-in process through an activity
    (context as? Activity)?.startActivityForResult(signInIntent, 100)
}

fun handleSignInResult(task: Task<GoogleSignInAccount>, auth: FirebaseAuth) {
    try {
        val account = task.getResult(ApiException::class.java)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("LoginScreen", "Google sign-in successful")
            } else {
                Log.e("LoginScreen", "Google sign-in failed", it.exception)
            }
        }
    } catch (e: ApiException) {
        Log.e("LoginScreen", "Google sign-in failed", e)
    }
}

