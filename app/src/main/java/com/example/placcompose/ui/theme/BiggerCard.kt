package com.example.placcompose.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.placcompose.dataclasses.OglasData

@Composable
fun BiggerCard(oglasData: OglasData) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .height(300.dp),
            elevation = CardDefaults.cardElevation(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Gray
            )
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    oglasData.firstImage?.let { imageUrl ->
                        val painter: Painter = rememberImagePainter(data = imageUrl)
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier
                                .size(150.dp)
                                .clip(RoundedCornerShape(15.dp))
                        )
                    }
                    oglasData.name?.let {
                        Text(
                            text = it,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(start = 8.dp),
                            maxLines = 1
                        )
                    }

                    Text(
                        text = "Lorem Ipsum is simply dy of crambled it to makeaaaaaaaaaaaa " +
                                "y dy of crambled it to makeaa" +
                                "y dy of crambled it to makeaay dy of crambled it to makeaay dy of crambled it to makeaay dy of crambled it to makeaay dy of crambled it to makeaay dy of crambled it to makeaa" +
                                "y dy of crambled it to makeaay dy of crambled it to makeaay dy of crambled it to makeaay dy of crambled it to makeaay dy of crambled it to makeaa" +
                                "y dy of crambled it to makeaay dy of crambled it to makeaay dy of crambled it to makeaay dy of crambled it to makeaay dy of crambled it to makeaa",
                        fontSize = 13.sp,
                        modifier = Modifier.padding(6.dp),
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Black
                    )
                }
            }


        }
    }
}