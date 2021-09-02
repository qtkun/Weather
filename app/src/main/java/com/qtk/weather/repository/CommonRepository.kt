package com.qtk.weather.repository

import com.qtk.weather.api.Api
import com.qtk.weather.bean.AirQualityBean
import com.qtk.weather.bean.DailyBean
import com.qtk.weather.bean.HourlyBean
import com.qtk.weather.bean.NowBean
import com.qtk.weather.retrofit.data.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class CommonRepository constructor(private val api: Api) {
    suspend fun getDailyWeather(location: String): Flow<DailyBean?> = flow {
        emit(api.getDailyWeather(location))
    }.map {
        when(it) {
            is ApiResult.Success -> {
                it.data?.results?.get(0)
            }
            else -> null
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getNowWeather(location: String): Flow<NowBean?> = flow {
        emit(api.getNowWeather(location))
    }.map {
        when(it) {
            is ApiResult.Success -> {
                it.data?.results?.get(0)
            }
            else -> null
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getHourlyWeather(location: String): Flow<HourlyBean?> = flow {
        emit(api.getHourlyWeather(location))
    }.map {
        when(it) {
            is ApiResult.Success -> {
                it.data?.results?.get(0)
            }
            else -> null
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getNowAir(location: String): Flow<AirQualityBean?> = flow {
        emit(api.getNowAir(location))
    }.map {
        when(it) {
            is ApiResult.Success -> {
                it.data?.results?.get(0)
            }
            else -> null
        }
    }.flowOn(Dispatchers.IO)
}