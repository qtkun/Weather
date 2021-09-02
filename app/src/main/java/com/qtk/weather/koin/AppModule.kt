package com.qtk.weather.koin

import com.qtk.weather.api.Api
import com.qtk.weather.contant.BASE_URL
import com.qtk.weather.repository.CommonRepository
import com.qtk.weather.retrofit.adapter.ApiResultCallAdapterFactory
import com.qtk.weather.retrofit.service.ApiService
import com.qtk.weather.viewmodel.WeatherModel
import com.squareup.moshi.Moshi
import okhttp3.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

//koin依赖注入viewModel初始化
val viewModelModule = module {
    viewModel { WeatherModel(get()) }
}

//koin依赖注入基础三方类初始化
val appModule = module {
    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(getRequestHeader())
            .addInterceptor(commonInterceptor())
            .addInterceptor(getHttpLoggingInterceptor())
            .addInterceptor(getCacheInterceptor())
            .addNetworkInterceptor(getCacheInterceptor())
            .cache(getCache(get()))
            .cookieJar(cookieJar)
            .build()
    }
    single {
        Retrofit.Builder()
            .client(get())
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(ApiResultCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
    single {
        Api(get())
    }
    factory {
        CommonRepository(get())
    }
    single {
        get<Retrofit>().create(ApiService::class.java)
    }
    single { Moshi.Builder().build() }
}