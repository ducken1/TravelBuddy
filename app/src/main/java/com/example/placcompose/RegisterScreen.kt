package com.example.placcompose

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.placcompose.dataclasses.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController, openDrawer: () -> Unit) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var isInvalidEmail by remember { mutableStateOf(false) }
    var passwordLength by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF8E3))
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.menu),
                contentDescription = "Menu",
                tint = Color(0xFFBA6565),
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        openDrawer()
                    }
            )
        }

        // LOGO
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .height(120.dp)
                .padding(top = 16.dp)
        )

        // UNDERLINED TEXT
        Text(
            text = "Registracija",
            fontSize = 18.sp,
            modifier = Modifier
                .padding(bottom = 8.dp, top = 16.dp)
        )

        // Custom underline
        Spacer(
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth(0.35f) // 50% of screen width
                .background(Color.Black)
                .padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // EMAIL INPUT
        OutlinedTextField(
            value = email,
            onValueChange = { email = it.filter { !it.isWhitespace() } },
            label = { Text("Email") },
            modifier = Modifier
                .width(280.dp)
                .padding(vertical = 8.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White, // notranje ozadje
                focusedBorderColor = if (isInvalidEmail) Color.Red else Color.Black,
                unfocusedBorderColor = if (isInvalidEmail) Color.Red else Color.Black,
                cursorColor = Color.Black,
                textColor = Color.Black
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.mail),
                    contentDescription = "Email icon"
                )
            }
        )

        // PASSWORD INPUT
        OutlinedTextField(
            value = password,
            onValueChange = { password = it.filter { it.isLetterOrDigit() } },
            label = { Text("Geslo") },
            modifier = Modifier
                .width(280.dp)
                .padding(vertical = 8.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(if (passwordVisible) R.drawable.visibilityoff else R.drawable.visibility),
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White, // Notranjost input polja
                focusedBorderColor =  if (passwordLength) Color.Red else Color.Black,
                unfocusedBorderColor =  if (passwordLength) Color.Red else Color.Black,
                cursorColor = Color.Black,
                textColor = Color.Black
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.password),
                    contentDescription = "Password icon"
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // REGISTER BUTTON
        OutlinedButton(
            onClick = {
                val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}".toRegex()
                isInvalidEmail = !email.matches(emailPattern)
                passwordLength = password.length < 6

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Prosimo izpolnite vsa polja", Toast.LENGTH_SHORT).show()
                } else if (isInvalidEmail) {
                    Toast.makeText(context, "Neustrezen email", Toast.LENGTH_SHORT).show()
                } else if (passwordLength) {
                    Toast.makeText(context, "Geslo mora vsebovati vsaj 6 znakov", Toast.LENGTH_SHORT).show()
                } else {
                    // Add your Firebase or other logic here
                    register(context, email, password, navController)
                }
            },
            modifier = Modifier
                .width(200.dp)
                .padding(vertical = 16.dp),
            shape = RectangleShape,
            border = BorderStroke(1.dp, Color.Black),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA6565))
        ) {
            Text("Ustvari račun", fontSize = 20.sp)
        }
    }
}


private fun register(
    context: Context,
    email: String,
    password: String,
    navController: NavHostController
) {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseDatabase = FirebaseDatabase.getInstance()
    val databaseReference = firebaseDatabase.reference.child("Users")

    databaseReference.orderByChild("mail").equalTo(email)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            val userId = firebaseAuth.currentUser?.uid
                            val userData = UserData(userId, email, password)
                            databaseReference.child(userId!!).setValue(userData)
                            Toast.makeText(context, "Registracija uspešna!", Toast.LENGTH_SHORT).show()
                            navController.navigate("home")
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Uporabnik s tem emailom že obstaja", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "Uporabnik s tem emailom že obstaja", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
}
