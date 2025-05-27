package com.example.placcompose.ui.theme

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
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

    var profilePictureUrl by remember { mutableStateOf<String?>(null) }


    var authState by remember { mutableStateOf(currentUser != null) }
    var userName by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // val sectionNames = listOf("", "Oglasi", "Račun")

    val openDrawer: () -> Unit = {
        scope.launch {
            drawerState.open()
        }
    }


    LaunchedEffect(authState) {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            authState = firebaseAuth.currentUser != null
        }
        auth.addAuthStateListener(authStateListener)

        if (authState && currentUser != null) {
            getUsername() { fetchedUserName ->
                userName = fetchedUserName
            }
            getProfilePictureUrl(currentUser.uid) { url ->
                profilePictureUrl = url
            }
        }
         else {
            userName = null
        }

    }

    val sections = if (authState) {
        listOf(
            listOf(
                DrawerData(icon = R.drawable.home, label = "Domov", isSelected = true),
                DrawerData(icon = R.drawable.chat, label = "Sporočila")
            ),
            listOf(
                DrawerData(icon = R.drawable.logout, label = "Odjava")
            )
        )
    } else {
        listOf(
            listOf(
                DrawerData(icon = R.drawable.home, label = "Domov", isSelected = true),
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
            ModalDrawerSheet(drawerContainerColor = Color(0xFFFEF8E3)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFEF8E3))
                        .padding(10.dp)
                ) {
                    // The profile section in the center
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp, horizontal = 5.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                        AsyncImage(
                            model = profilePictureUrl ?: R.drawable.avatar,
                            contentDescription = "Profilna slika",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                        )
                        Text(
                            text = userName ?: "Mr. X",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.Black,
                            fontSize = 28.sp,
                            modifier = Modifier.padding(start = 10.dp, bottom = 5.dp)
                        )
                    }
                        }

                    // Settings icon at top right
                    if (authState) {
                        Icon(
                            painter = painterResource(id = R.drawable.settings),
                            contentDescription = "Settings",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .clickable {
                                    scope.launch {
                                        drawerState.close() // 👈 Close the drawer
                                        navController.navigate("settings")
                                    }
                                }
                        )
                    }
                }



                sections.forEachIndexed { sectionIndex, sectionItems ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(Color.Black)
                    )

                    sectionItems.forEachIndexed { itemIndex, item ->

                        NavigationDrawerItem(modifier = Modifier.padding(
                            vertical = 0.dp, horizontal = 0.dp
                        ),
                            label = { Text(text = item.label) },
                            selected = selectedItem == item,
                            onClick = {

                                scope.launch { drawerState.close() }

                                selectedItem = item

                                when (item.label) {
                                    "Domov" -> navController.navigate("home")
                                    "Sporočila" -> navController.navigate("chat_list")  // tukaj nov vnos
                                    "Prijava" -> navController.navigate("login")
                                    "Registracija" -> navController.navigate("register")
                                    "Odjava" -> {
                                        profilePictureUrl = null
                                        auth.signOut()
                                        navController.navigate("home")
                                        Toast.makeText(
                                            context,
                                            "Odjava uspešna",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
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
                                unselectedTextColor = Color(0xFFBA6565),
                                unselectedContainerColor = Color(0xFFFEF8E3),
                                selectedContainerColor = Color(0xFFBA6565),
                                selectedIconColor = Color.White,
                                unselectedIconColor = Color(0xFFBA6565)
                            ),
                            shape = RectangleShape
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.7.dp)
                                .background(Color.Black)
                        )
                    }
                }
            }
        },
        content = {

            NavGraph(navController, openDrawer)
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
                    val userName = dataSnapshot.child("name").getValue(String::class.java)
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

fun getProfilePictureUrl(userId: String?, onResult: (String?) -> Unit) {
    if (userId == null) {
        onResult(null)
        return
    }

    val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val profileUrl = snapshot.child("profilepicture").getValue(String::class.java)
            onResult(profileUrl)
        }

        override fun onCancelled(error: DatabaseError) {
            onResult(null)
        }
    })
}



