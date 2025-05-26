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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController, openDrawer: () -> Unit) {

    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val Regex = Regex("""^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$""")

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
                    .size(40.dp)
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
            text = "Prijava",
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

        // Email input field
        OutlinedTextField(
            value = email,
            onValueChange = { newEmail ->
                email = newEmail.filter { !it.isWhitespace() }
            },
            label = { Text("Email") },
            modifier = Modifier
                .width(280.dp)
                .padding(vertical = 8.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
                focusedBorderColor =  Color.Black,
                unfocusedBorderColor = Color.Black,
                cursorColor = Color.Black,
                textColor = Color.Black
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.mail),
                    contentDescription = "R.drawable.logout"
                )
            }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { newText ->
                password = newText.filter { it.isLetterOrDigit() }
            },
            label = { Text("Geslo") },
            modifier = Modifier
                .width(280.dp)
                .padding(vertical = 8.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
                focusedBorderColor =  Color.Black,
                unfocusedBorderColor = Color.Black,
                cursorColor = Color.Black,
                textColor = Color.Black
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.password),
                    contentDescription = "R.drawable.logout"
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    content = {
                        if (passwordVisible) {
                            Icon(
                                painter = painterResource(R.drawable.visibilityoff),
                                contentDescription = "R.drawable.logout"
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.visibility),
                                contentDescription = "R.drawable.logout"
                            )
                        }
                    }
                )
            }
        )

        Spacer(modifier = Modifier.height(25.dp))

        OutlinedButton(
            onClick = {

                val formattedEmail = email.trim().replace("\\s+".toRegex(), "")

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Nekatera polja so prazna", Toast.LENGTH_SHORT).show()
                } else if (!Regex.matches(formattedEmail)) {
                    Toast.makeText(context, "Neustrezen email format", Toast.LENGTH_SHORT).show()
                }
                else {
                    email = formattedEmail
                    login(context, email, password, navController)
                }

            },
            modifier = Modifier
                .width(200.dp)
                .padding(vertical = 16.dp),
            shape = RectangleShape,
            border = BorderStroke(1.dp, Color.Black),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA6565))
        ) {
            Text(text = "Prijava", fontSize = 20.sp)
        }

        OutlinedButton(
            onClick = {
                navController.navigate("register")
            },
            modifier = Modifier
                .width(200.dp)
                .padding(bottom = 16.dp),
            shape = RectangleShape,
            border = BorderStroke(1.dp, Color.Black),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA6565))
        ) {
            Text(text = "Ustvari račun", fontSize = 20.sp)
        }


    }
}

private fun login(
    context: Context,
    mail: String,
    password: String,
    navController: NavHostController
) {

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference

    firebaseAuth = FirebaseAuth.getInstance()
    firebaseDatabase = FirebaseDatabase.getInstance()
    databaseReference = firebaseDatabase.reference.child("Users")

    databaseReference.orderByChild("mail").equalTo(mail).addListenerForSingleValueEvent(object :
        ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                for (userSnapshot in dataSnapshot.children) {
                    firebaseAuth.signInWithEmailAndPassword(mail, password).addOnSuccessListener {

                        Toast.makeText(context, "Prijava uspešna!", Toast.LENGTH_SHORT).show()

                        navController.navigate("home")

                    }.addOnFailureListener {
                        Toast.makeText(context, "Napačno geslo ali email", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                Toast.makeText(
                    context,
                    "Uporabnik ne obstaja",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Toast.makeText(context, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT)
                .show()
        }
    })
}