package com.example.placcompose

import AgeDropdown
import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController, openDrawer: () -> Unit) {

    val context = LocalContext.current

    var text by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var imageUrl by remember { mutableStateOf<String?>(null) }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    val city1 = remember { mutableStateOf("") }
    val city2 = remember { mutableStateOf("") }
    val city3 = remember { mutableStateOf("") }

    val startDate1 = remember { mutableStateOf("") }
    val endDate1 = remember { mutableStateOf("") }
    val startDate2 = remember { mutableStateOf("") }
    val endDate2 = remember { mutableStateOf("") }
    val startDate3 = remember { mutableStateOf("") }
    val endDate3 = remember { mutableStateOf("") }

    val cities = listOf(
        Triple(city1, startDate1, endDate1),
        Triple(city2, startDate2, endDate2),
        Triple(city3, startDate3, endDate3)
    )

    var isButtonEnabled by remember { mutableStateOf(true) }

    val pickImage =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            it?.let { uri ->
                imageUri = uri
            }
        }

    LaunchedEffect(Unit) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid
        val databaseRef = FirebaseDatabase.getInstance().reference

        if (userId != null) {
            // Load existing profile picture URL
            databaseRef.child("Users").child(userId).child("profilepicture").get()
                .addOnSuccessListener { snapshot ->
                    val url = snapshot.getValue(String::class.java)
                    if (!url.isNullOrBlank()) {
                        imageUrl = url
                    }
                }
            // Load existing name
            databaseRef.child("Users").child(userId).child("name").get()
                .addOnSuccessListener { snapshot ->
                    val savedName = snapshot.getValue(String::class.java)
                    if (!savedName.isNullOrBlank()) {
                        name = savedName
                    }
                }

            databaseRef.child("Users").child(userId).child("age").get()
                .addOnSuccessListener { snapshot ->
                    val savedAge = snapshot.getValue(String::class.java)
                    if (!savedAge.isNullOrBlank()) {
                        age = savedAge
                    }
                }

            databaseRef.child("Users").child(userId).child("bio").get()
                .addOnSuccessListener { snapshot ->
                    val savedBio = snapshot.getValue(String::class.java)
                    if (!savedBio.isNullOrBlank()) {
                        bio = savedBio
                    }
                }

            // Load cities
            val citiesRef = databaseRef.child("Users").child(userId).child("cities")
            citiesRef.get().addOnSuccessListener { snapshot ->
                snapshot.children.forEachIndexed { index, citySnapshot ->
                    val cityName = citySnapshot.child("name").getValue(String::class.java) ?: ""
                    val startDate = citySnapshot.child("startDate").getValue(String::class.java) ?: ""
                    val endDate = citySnapshot.child("endDate").getValue(String::class.java) ?: ""

                    // Safely set values if index is in bounds
                    if (index < cities.size) {
                        val (cityState, startState, endState) = cities[index]
                        cityState.value = cityName
                        startState.value = startDate
                        endState.value = endDate
                    }
                }
            }
        }
    }

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
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(40.dp)
                    .padding()
            )
        }

        // Row with image on left and name input on right
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val painter = rememberImagePainter(
                data = imageUri ?: imageUrl ?: R.drawable.autist
            )

            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { pickImage.launch("image/*") }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .padding(start = 5.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = "Ime") },
                    modifier = Modifier
                        .widthIn(min = 50.dp, max = 145.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                AgeDropdown(age = age, onAgeSelected = { age = it })
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Opis") },
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            singleLine = false,
            maxLines = 5
        )


        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {

                cities.forEachIndexed { index, (cityState, startDate, endDate) ->

                    val labelText = "Mesto ${index + 1}"
                    val context = LocalContext.current
                    val calendar = Calendar.getInstance()


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = cityState.value,
                            onValueChange = { cityState.value = it },
                            label = { Text(labelText) },
                            modifier = Modifier.width(280.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            painter = painterResource(id = R.drawable.start_date),
                            contentDescription = "Start date",
                            tint = Color(0xFFBA6565),
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            startDate.value = "$dayOfMonth.${month + 1}.$year"
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            painter = painterResource(id = R.drawable.end_date),
                            contentDescription = "End date",
                            tint = Color(0xFFBA6565),
                            modifier = Modifier
                                .size(24.dp)
                                .padding(bottom = 2.dp)
                                .clickable {
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            endDate.value = "$dayOfMonth.${month + 1}.$year"
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                        )
                    }

                    // Po želji dodaš prikaz izbranih datumov:
                    Text("Od: ${startDate.value}  Do: ${endDate.value}", fontSize = 12.sp, color = Color(0xFFBA6565))
                }
            }
        }



        OutlinedButton(
            onClick = {
                if (isButtonEnabled) {
                    isButtonEnabled = false
                    saveUserProfile(
                        name = name,
                        age = age,
                        imageUri = imageUri,
                        navController = navController,
                        context = context,
                        isButtonEnabled = mutableStateOf(isButtonEnabled),
                        bio = bio,
                        cities = cities
                    )
                }
            },
            modifier = Modifier
                .width(200.dp)
                .padding(top = 12.dp),
            shape = RectangleShape,
            border = BorderStroke(1.dp, Color.Black),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFBA6565))
        ) {
            Text(text = "Shrani", fontSize = 20.sp)
        }
    }
}

private fun saveUserProfile(
    name: String,
    age: String,
    imageUri: Uri?,
    navController: NavHostController,
    context: Context,
    isButtonEnabled: MutableState<Boolean>,
    bio: String,
    cities: List<Triple<MutableState<String>, MutableState<String>, MutableState<String>>> // (city, startDate, endDate)
) {
    val firebaseAuth = FirebaseAuth.getInstance()
    val userId = firebaseAuth.currentUser?.uid
    val databaseRef = FirebaseDatabase.getInstance().reference.child("Users")

    if (userId == null) {
        Toast.makeText(context, "Uporabnik ni prijavljen", Toast.LENGTH_SHORT).show()
        isButtonEnabled.value = true
        return
    }


    databaseRef.child(userId).child("name").setValue(name)
    databaseRef.child(userId).child("age").setValue(age)
    databaseRef.child(userId).child("bio").setValue(bio)

    // Save city info
    val citiesRef = databaseRef.child(userId).child("cities")
    cities.forEachIndexed { index, (cityName, startDate, endDate) ->
        val cityKey = "city${index + 1}"
        citiesRef.child(cityKey).child("name").setValue(cityName.value)
        citiesRef.child(cityKey).child("startDate").setValue(startDate.value)
        citiesRef.child(cityKey).child("endDate").setValue(endDate.value)
    }



    // If image selected, upload and save url
    if (imageUri != null) {
        val storageRef = FirebaseStorage.getInstance().reference.child("Images")
        val imageId = databaseRef.push().key ?: userId

        storageRef.child(imageId).putFile(imageUri)
            .addOnSuccessListener { task ->
                task.metadata!!.reference!!.downloadUrl.addOnSuccessListener { url ->
                    val imageUrl = url.toString()
                    databaseRef.child(userId).child("profilepicture").setValue(imageUrl)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Profilna slika uspešno posodobljena", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Napaka pri shranjevanju slike", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Napaka pri nalaganju slike", Toast.LENGTH_SHORT).show()
            }
    }

    isButtonEnabled.value = true
}
