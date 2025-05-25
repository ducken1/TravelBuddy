package com.example.placcompose.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.placcompose.dataclasses.CityData

@Composable
fun CityInfoRow(city: CityData) {
    Column {
        Text(
            text = city.name ?: "N/A",
            fontSize = 12.sp,
            color = Color(0xFFBA6565)
        )
        Text(
            text = "${city.startDate ?: "?"} - ${city.endDate ?: "?"}",
            fontSize = 12.sp,
            color = Color(0xFF666666)
        )
    }
}
