package com.qtk.weather.utils

import android.Manifest
import android.app.Activity
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Address
import android.location.Criteria
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.util.TypedValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.qtk.weather.application.App
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by qtkun
on 2020-06-16.
 */
fun Long.toDateString(dateFormat: Int = DateFormat.MEDIUM): String {
    val df = DateFormat.getDateInstance(dateFormat, Locale.getDefault())
    return df.format(this)
}

fun currentTime(): String {
    val format = DecimalFormat("00")
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val calendar = Calendar.getInstance()
    val month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) ?: ""
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: ""
    val amPm = calendar.getDisplayName(Calendar.AM_PM, Calendar.SHORT, Locale.getDefault()) ?: ""
    val hour = calendar[Calendar.HOUR]
    val minute = format.format(calendar[Calendar.MINUTE])
    return "$month${day}日，$dayOfWeek $amPm$hour:$minute"
}

fun String.toHourString(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = sdf.parse(this)?.time  ?: 0L
    val amPm = calendar.getDisplayName(Calendar.AM_PM, Calendar.SHORT, Locale.getDefault()) ?: ""
    val hour = calendar[Calendar.HOUR]
    return "$amPm${hour}时"
}

fun String.dayOfWeek(): String{
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = sdf.parse(this)?.time  ?: 0L
    if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == calendar.get(Calendar.DAY_OF_WEEK)) {
        return "今天"
    }
    return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) ?: ""
}

inline fun <reified T> T.dpToPx(): Float {
    val value = when (T::class) {
        Float::class -> this as Float
        Int::class -> this as Int
        else -> throw IllegalStateException("Type not supported")
    }
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value.toFloat(), App.instance.applicationContext.resources.displayMetrics
    )
}

fun locateAddress(activity: Activity): Address? {
    var address: Address? = null
    if (ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        val mLocationManager :LocationManager = activity.getSystemService(LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.isAltitudeRequired = false
        criteria.isBearingRequired = false
        criteria.isCostAllowed = true
        val provider = mLocationManager.getBestProvider(criteria,true)
        provider?.let {
            val location = mLocationManager.getLastKnownLocation(it)
            location?.let { loc ->
                val gc = Geocoder(activity)
                val result = gc.getFromLocation(loc.latitude, loc.longitude, 1)
                address = result?.get(0)
            }
        }
    }
    return address
}

val locationPermission = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION)