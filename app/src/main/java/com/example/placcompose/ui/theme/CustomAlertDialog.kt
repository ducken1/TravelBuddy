package com.example.placcompose.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CustomAlertDialog(
    onYesClick: () -> Unit,
    onNoClick: () -> Unit
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .width(370.dp)
                .height(140.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(10.dp),
            colors = CardDefaults.cardColors(Color(187, 187, 187, 255))

        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Delete item permanently", modifier = Modifier.padding(top = 5.dp))
                Text("Are you sure you want to delete this item?", modifier = Modifier.padding(top = 5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Box(modifier = Modifier
                        .width(90.dp)
                        .height(40.dp)
                        .padding(start = 12.dp)
                        .border(2.dp, Color.DarkGray, RoundedCornerShape(12.dp))) {
                        Button(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .width(90.dp)
                                .height(40.dp),

                            onClick = {
                                onYesClick()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(133, 133, 133))
                        ) {
                            Text("Yes", color = Color(0,250,0))
                        }
                    }

                    Box(modifier = Modifier
                        .width(90.dp)
                        .height(40.dp)
                        .padding(end = 12.dp)
                        .border(2.dp, Color.DarkGray, RoundedCornerShape(12.dp))) {
                        Button(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .width(90.dp)
                                .height(40.dp),
                            onClick = {
                                onNoClick()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(133, 133, 133))
                        ) {
                            Text("No", color = Color(250, 0 ,0))
                        }
                    }

                }
            }
        }
    }
}