package com.qtk.weather.viewmodel

import android.app.Activity
import android.location.Address
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qtk.weather.bean.AirQualityBean
import com.qtk.weather.bean.DailyBean
import com.qtk.weather.bean.HourlyBean
import com.qtk.weather.bean.NowBean
import com.qtk.weather.contant.DEFAULT_CITY
import com.qtk.weather.repository.CommonRepository
import com.qtk.weather.utils.locateAddress
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class WeatherModel(private val repository: CommonRepository): ViewModel() {
    private val _dailyLoading = MutableLiveData<Boolean>()
    private val _nowLoading = MutableLiveData<Boolean>()
    private val _hourlyLoading = MutableLiveData<Boolean>()
    private val _airLoading = MutableLiveData<Boolean>()
    private val _loading = MediatorLiveData<Boolean>()
    val loading get() = _loading

    private val _dailyBean = MutableLiveData<DailyBean>()
    val dailyBean get() = _dailyBean

    private val _nowBean = MutableLiveData<NowBean>()
    val nowBean get() = _nowBean

    private val _hourlyBean = MutableLiveData<HourlyBean>()
    val hourlyBean get() = _hourlyBean

    private val _airBean = MutableLiveData<AirQualityBean>()
    val airBean get() = _airBean

    private val _address = MutableLiveData<Address>()
    val address get() = _address

    init {
        _loading.addSource(_dailyLoading) {
            _loading.postValue(it or (_nowLoading.value ?: false)
                    or (_hourlyLoading.value ?: false)
                    or (_airLoading.value ?: false))
        }
        _loading.addSource(_nowLoading) {
            _loading.postValue((_dailyLoading.value ?: false) or it
                    or (_hourlyLoading.value ?: false)
                    or (_airLoading.value ?: false))
        }
        _loading.addSource(_hourlyLoading) {
            _loading.postValue((_dailyLoading.value ?: false)
                    or (_nowLoading.value ?: false) or it
                    or (_airLoading.value ?: false))
        }
        _loading.addSource(_airLoading) {
            _loading.postValue((_dailyLoading.value ?: false)
                    or (_hourlyLoading.value ?: false)
                    or (_nowLoading.value ?: false) or it)
        }
        _loading.addSource(_address) {
            viewModelScope.launch {
                getWeatherData("${it.latitude}:${it.longitude}")
            }
        }
    }

    private fun getDailyWeather(location: String) = viewModelScope.launch {
        repository.getDailyWeather(location)
            .onStart {
                _dailyLoading.postValue(true)
            }
            .collectLatest {
                println(it.toString())
                _dailyBean.postValue(it)
                _dailyLoading.postValue(false)
            }
    }

    private fun getNowWeather(location: String) = viewModelScope.launch {
        repository.getNowWeather(location)
            .onStart {
                _nowLoading.postValue(true)
            }
            .collectLatest {
                println(it.toString())
                _nowBean.postValue(it)
                _nowLoading.postValue(false)
            }
    }

    private fun getHourlyWeather(location: String) = viewModelScope.launch {
        repository.getHourlyWeather(location)
            .onStart {
                _hourlyLoading.postValue(true)
            }
            .collectLatest {
                println(it.toString())
                _hourlyBean.postValue(it)
                _hourlyLoading.postValue(false)
            }
    }

    private fun getNowAir(location: String) = viewModelScope.launch {
        repository.getNowAir(location)
            .onStart {
                _airLoading.postValue(true)
            }
            .collectLatest {
                println(it.toString())
                _airBean.postValue(it)
                _airLoading.postValue(false)
            }
    }

     suspend fun getWeatherData(location: String = DEFAULT_CITY) = coroutineScope {
        getDailyWeather(location)
        getNowWeather(location)
        getHourlyWeather(location)
        getNowAir(location)
    }

    fun getAddress(activity: Activity) {
        locateAddress(activity)?.also {
            address.postValue(it)
        }
    }
}