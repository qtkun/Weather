package com.qtk.weather.api

import com.qtk.weather.bean.AirQualityBean
import com.qtk.weather.bean.DailyBean
import com.qtk.weather.bean.HourlyBean
import com.qtk.weather.bean.NowBean
import com.qtk.weather.retrofit.data.ApiResult
import com.qtk.weather.retrofit.data.ResponseResult
import com.qtk.weather.retrofit.service.ApiService

class Api constructor(private val service: ApiService){
    suspend fun getDailyWeather(location: String): ApiResult<ResponseResult<DailyBean>> {
        return service.getDailyWeather(location = location)
    }

    suspend fun getNowWeather(location: String): ApiResult<ResponseResult<NowBean>> {
        return service.getNowWeather(location = location)
    }

    suspend fun getHourlyWeather(location: String): ApiResult<ResponseResult<HourlyBean>> {
        return service.getHourlyWeather(location = location)
    }

    suspend fun getNowAir(location: String): ApiResult<ResponseResult<AirQualityBean>> {
        return service.getNowAir(location = location)
    }
}