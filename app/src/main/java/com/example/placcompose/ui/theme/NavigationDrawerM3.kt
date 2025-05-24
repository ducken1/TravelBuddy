package com.example.placcompose.ui.theme

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.placcompose.R
import com.example.placcompose.navigation.NavGraph
import com.example.placcompose.dataclasses.DrawerData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawerM3(navController: NavHostController) {

    val auth = FirebaseAuth.getInstance()

    val currentUser = FirebaseAuth.getInstance().currentUser

    var authState by remember { mutableStateOf(currentUser != null) }
    var userName by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // val sectionNames = listOf("", "Oglasi", "Račun")

    LaunchedEffect(authState) {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            authState = firebaseAuth.currentUser != null
        }
        auth.addAuthStateListener(authStateListener)

        if (authState) {
            getUsername() { fetchedUserName ->
                userName = fetchedUserName
            }
        } else {
            userName = null
        }

    }

    val sections = if (authState) {
        listOf(
            listOf(
                DrawerData(icon = R.drawable.home, label = "Domov", isSelected = true),
                DrawerData(icon = R.drawable.inbox, label = "Sporočila")
            ),
            listOf(
                DrawerData(icon = R.drawable.dodajoglas, label = "Dodaj Oglas"),
                DrawerData(icon = R.drawable.mojioglasi, label = "Moji Oglasi")
            ),
            listOf(
                DrawerData(icon = R.drawable.logout, label = "Odjava")
            )
        )
    } else {
        listOf(
            listOf(
                DrawerData(icon = R.drawable.home, label = "Domov", isSelected = true),
                DrawerData(icon = R.drawable.inbox, label = "Sporočila")
            ), listOf(
                DrawerData(icon = R.drawable.login, label = "Prijava"),
                DrawerData(icon = R.drawable.register, label = "Registracija")
            )
        )
    }


    var selectedItem by remember {
        mutableStateOf(sections.flatten().find { it.isSelected } ?: sections[0][0])
    }

    ModalNavigationDrawer(scrimColor = Color(90, 90, 90),
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = Color.DarkGray) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.sweepGradient(
                                colors = listOf(Color(34, 120, 37), Color(11, 90, 7)),
                                Offset(80f, 100f)
                            )
                        )

                ) {

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.autist),
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .padding(vertical = 10.dp)
                        )
                        Text(
                            text = userName ?: "Gospod Ježek",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.Black,
                            fontSize = 28.sp,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                }


                sections.forEachIndexed { sectionIndex, sectionItems ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(Color.Black)
                    )

                    sectionItems.forEachIndexed { itemIndex, item ->

                        NavigationDrawerItem(modifier = Modifier.padding(
                            vertical = 4.dp, horizontal = 3.dp
                        ),
                            label = { Text(text = item.label) },
                            selected = selectedItem == item,
                            onClick = {

                                scope.launch { drawerState.close() }

                                selectedItem = item

                                when (item.label) {
                                    "Domov" -> navController.navigate("home")
                                    "Prijava" -> navController.navigate("login")
                                    "Registracija" -> navController.navigate("register")
                                    "Odjava" -> {
                                        auth.signOut()
                                        navController.navigate("home")
                                        Toast.makeText(
                                            context,
                                            "Odjava uspešna",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    "Dodaj Oglas" -> navController.navigate("dodajoglas")
                                    "Moji Oglasi" -> navController.navigate("mojioglasi")
                                }

                            },
                            icon = {
                                Icon(
                                    painter = painterResource(item.icon),
                                    contentDescription = item.label
                                )
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedTextColor = Color.White,
                                unselectedTextColor = Color(0, 210, 0),
                                unselectedContainerColor = Color.DarkGray,
                                selectedContainerColor = Color(0, 120, 0, 148),
                                selectedIconColor = Color.White,
                                unselectedIconColor = Color(0, 210, 0)
                            )
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.7f.dp)
                                .background(Color.Black)
                        )
                    }
                }
            }
        },
        content = {

            NavGraph(navController)
        })

}

fun getUsername(callback: (String?) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid

    if (userId != null) {
        val userReference = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userName = dataSnapshot.child("mail").getValue(String::class.java)
                    callback(userName)
                } else {
                    callback(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(null)
            }
        })
    } else {
        callback(null)
    }
}


