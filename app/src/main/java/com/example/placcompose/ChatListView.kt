package com.example.placcompose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.placcompose.dataclasses.ChatSummary
import com.example.placcompose.viewmodel.ChatListViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import android.net.Uri
import androidx.compose.foundation.background


import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

@Composable
fun ChatListView(navController: NavHostController, openDrawer: () -> Unit) {

    val chatListViewModelViewModel: ChatListViewModel = viewModel()

    val chatList by chatListViewModelViewModel.chatList.collectAsState()
    val context = LocalContext.current

    // Stanje za prikaz potrditvenega dialoga in katero klepet izbrisati
    val showDeleteDialog = remember { mutableStateOf(false) }
    val chatToDelete = remember { mutableStateOf<ChatSummary?>(null) }


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
                    .clickable { openDrawer() }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Klepet",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color(0xFFBA6565),
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Divider(
            color = Color(0xFFBA6565),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )



        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFEF8E3))
        ) {
            items(chatList, key = { it.chatId }) { chat ->
                ChatListItem(
                    chatSummary = chat,
                    onClick = {
                        val encodedUserName = Uri.encode(chat.userName)
                        val encodedProfilePicUrl = Uri.encode(chat.profilePictureUrl)
                        navController.navigate("chat/${chat.otherUserId}/$encodedUserName/$encodedProfilePicUrl")
                    },
                    onDelete = {
                        // Prikaži potrditveno okno
                        chatToDelete.value = chat
                        showDeleteDialog.value = true
                    }
                )
            }
        }

    }
    // AlertDialog za potrditev izbrisa
    if (showDeleteDialog.value && chatToDelete.value != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Izbriši klepet") },
            text = { Text("Ali ste prepričani, da želite izbrisati klepet z uporabnikom \"${chatToDelete.value?.userName}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    chatToDelete.value?.let {
                        chatListViewModelViewModel.deleteChat(it.chatId)
                        Toast.makeText(context, "Klepet izbrisan", Toast.LENGTH_SHORT).show()
                    }
                    showDeleteDialog.value = false
                    chatToDelete.value = null
                }) {
                    Text("Da")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog.value = false
                    chatToDelete.value = null
                }) {
                    Text("Ne")
                }
            }
        )
    }
}

@Composable
fun ChatListItem(
    chatSummary: ChatSummary,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onDoubleTap = { onDelete() }
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 12.dp), // Reduced padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = chatSummary.profilePictureUrl.takeIf { it.isNotBlank() },
                placeholder = painterResource(id = R.drawable.avatar),
                error = painterResource(id = R.drawable.avatar),
                contentDescription = "Profilna slika",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp) // Slightly smaller
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = chatSummary.userName.ifBlank { chatSummary.otherUserId },
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }

        // Thin divider
        Divider(
            color = Color(0xFFBA6565), // Menu icon color
            thickness = 0.5.dp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}



