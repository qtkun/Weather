package com.qtk.weather.retrofit.service

import com.qtk.weather.bean.AirQualityBean
import com.qtk.weather.bean.DailyBean
import com.qtk.weather.bean.HourlyBean
import com.qtk.weather.bean.NowBean
import com.qtk.weather.contant.API_KEY
import com.qtk.weather.retrofit.data.ApiResult
import com.qtk.weather.retrofit.data.ResponseResult
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("v3/weather/daily.json")
    suspend fun getDailyWeather(
        @Query("key") key: String = API_KEY,
        @Query("location") location: String,
        @Query("start") start: Int = -1,
        @Query("days") days: Int = 7
    ): ApiResult<ResponseResult<DailyBean>>

    @GET("v3/weather/now.json")
    suspend fun getNowWeather(
        @Query("key") key: String = API_KEY,
        @Query("location") location: String
    ): ApiResult<ResponseResult<NowBean>>

    @GET("v3/weather/hourly.json")
    suspend fun getHourlyWeather(
        @Query("key") key: String = API_KEY,
        @Query("location") location: String
    ): ApiResult<ResponseResult<HourlyBean>>

    @GET("v3/air/now.json")
    suspend fun getNowAir(
        @Query("key") key: String = API_KEY,
        @Query("location") location: String
    ): ApiResult<ResponseResult<AirQualityBean>>
}