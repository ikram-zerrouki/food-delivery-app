package com.example.projettdm

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.mongodb.client.model.Filters
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.bson.Document

class AuthService {
    fun register(name: String, email: String, pwd: String, context: Context, navController: NavController) {
        val usersCollection = getUsersCollection("Users")

        // Check if the email already exists in the collection by counting matching documents
        runBlocking {
            try {
                val count = usersCollection.countDocuments(Document("email", email)).awaitFirstOrNull() ?: 0
                if (count > 0) {
                    // Show a toast message if the email already exists
                    Toast.makeText(context, "Email already exists", Toast.LENGTH_SHORT).show()
                } else {
                    // If email doesn't exist, proceed to insert the user
                    val userDocument = Document().apply {
                        append("name", name)
                        append("email", email)
                        append("pwd", pwd)
                    }
                    val result = usersCollection.insertOne(userDocument).awaitFirstOrNull()

                    if (result != null) {
                        // If the insertion is successful, navigate to the home screen
                        println("User inserted successfully!")
                        navController.navigate("home")
                    } else {
                        // Handle failure in insertion if necessary
                        println("Error inserting user!")
                    }
                }
            } catch (e: Exception) {
                // Handle error
                println("Error inserting user: ${e.message}")
            }
        }
    }


    fun signIn(email: String, password: String, context: Context, navController: NavController) {
        val usersCollection = getUsersCollection("Users")

        val filter = Filters.and(
            Filters.eq("email", email),
            Filters.eq("pwd", password)
        )

        runBlocking {
            try {
                val userCount = usersCollection.countDocuments(filter).awaitFirstOrNull()
                if (userCount == 1L) {
                    val userDocument = usersCollection.find(filter).awaitFirstOrNull()
                    val userId = userDocument?.getObjectId("_id").toString()
                    navController.navigate("homeScreen/$userId")
                } else {
                    Toast.makeText(context, "Les informations de connexion sont erronées", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                println("Error during sign-in: ${e.message}")
            }
        }
    }

}