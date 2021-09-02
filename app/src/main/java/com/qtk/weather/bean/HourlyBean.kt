package com.qtk.weather.bean

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class HourlyBean(
    val hourly: List<Hourly>,
    val location: Location
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Hourly(
    val code: String,
    val humidity: String,
    val temperature: String,
    val text: String,
    val time: String,
    val wind_direction: String,
    val wind_speed: String
): Parcelable