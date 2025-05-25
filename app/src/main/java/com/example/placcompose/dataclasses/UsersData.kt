package com.example.placcompose.dataclasses

data class UsersData(
    val age: String? = null,
    val bio: String? = null,
    val id: String? = null,
    val mail: String? = null,
    val name: String? = null,
    val password: String? = null,
    val profilepicture: String? = null,
    val cities: CitiesData? = null
)

data class CitiesData(
    val city1: CityData? = null,
    val city2: CityData? = null,
    val city3: CityData? = null
)

data class CityData(
    val name: String? = null,
    val startDate: String? = null,
    val endDate: String? = null
)