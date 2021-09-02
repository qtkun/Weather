package com.qtk.weather.bean

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class AirQualityBean(
    val air: Air,
    val last_update: String,
    val location: Location
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Air(
    val city: City
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class City(
    val aqi: String,
    val co: String,
    val last_update: String,
    val no2: String,
    val o3: String,
    val pm10: String,
    val pm25: String,
    val primary_pollutant: String,
    val quality: String,
    val so2: String
): Parcelable