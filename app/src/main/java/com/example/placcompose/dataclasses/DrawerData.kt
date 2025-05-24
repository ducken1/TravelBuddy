package com.example.placcompose.dataclasses

data class DrawerData(
    val icon: Int,
    var label: String,
    var isSelected: Boolean = false
)