package com.qtk.weather.bean

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class DailyBean(
    val daily: List<Daily>,
    val last_update: String,
    val location: Location
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Daily(
    val code_day: String,
    val code_night: String,
    val date: String,
    val high: String,
    val humidity: String,
    val low: String,
    val precip: String,
    val rainfall: String,
    val text_day: String,
    val text_night: String,
    val wind_direction: String,
    val wind_direction_degree: String,
    val wind_scale: String,
    val wind_speed: String
): Parcelable
