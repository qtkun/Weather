package com.qtk.weather.bean

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class Location(
    val country: String,
    val id: String,
    val name: String,
    val path: String,
    val timezone: String,
    val timezone_offset: String
): Parcelable