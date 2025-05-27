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
import androidx.compose.runtime.LaunchedEffect
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
import com.google.firebase.auth.FirebaseAuth
import java.util.Date
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun ChatListView(navController: NavHostController, viewModel: ChatListViewModel = viewModel()) {
    val chatList by viewModel.chatList.collectAsState()
    val context = LocalContext.current

    // Stanje za prikaz potrditvenega dialoga in katero klepet izbrisati
    val showDeleteDialog = remember { mutableStateOf(false) }
    val chatToDelete = remember { mutableStateOf<ChatSummary?>(null) }

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
            Divider()
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
                        viewModel.deleteChat(it.chatId)
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onDoubleTap = { onDelete() }
                )
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = chatSummary.profilePictureUrl.takeIf { it.isNotBlank() },
            placeholder = painterResource(id = R.drawable.autist),
            error = painterResource(id = R.drawable.autist),
            contentDescription = "Profilna slika",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = chatSummary.userName.ifBlank { chatSummary.otherUserId },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


