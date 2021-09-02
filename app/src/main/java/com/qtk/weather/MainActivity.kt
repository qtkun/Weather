package com.qtk.weather

import android.Manifest
import android.location.Address
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.qtk.weather.bean.*
import com.qtk.weather.ui.SwipeToRefreshLayout
import com.qtk.weather.ui.theme.*
import com.qtk.weather.utils.*
import com.qtk.weather.viewmodel.WeatherModel
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import java.util.*

class MainActivity : ComponentActivity() {
    private val weatherModel by inject<WeatherModel> ()
    private val columnModifier = Modifier
        .fillMaxWidth()
        .padding(top = 15.dp, start = 15.dp, end = 15.dp)
        .background(
            color = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
        .padding(25.dp)
    private val backgroundLocation = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        it?.let {
            toast(it.toString())
        }
    }

    private val coarseLocation = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (it[Manifest.permission.ACCESS_COARSE_LOCATION] == true &&
            it[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            weatherModel.getAddress(this)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                backgroundLocation.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val city by weatherModel.address.observeAsState()
            WeatherTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                        Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                title = {
                                    city?.let {
                                        Text(
                                            text = it.locality,
                                            fontSize = 18.sp,
                                            color = TextBlack
                                        )
                                    }
                                },
                                Modifier.fillMaxWidth(),
                                navigationIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_baseline_location),
                                        contentDescription = "location",
                                        Modifier.padding(start = 15.dp)
                                    )
                                },
                                backgroundColor = BgGray,
                                elevation = 0.dp,
                            )
                        }
                    ) {
                        Body()
                    }
                }
            }
        }
        coarseLocation.launch(locationPermission)
    }

    @Preview(showBackground = true)
    @Composable
    fun Body() {
        val dailyBean by weatherModel.dailyBean.observeAsState()
        val nowBean by weatherModel.nowBean.observeAsState()
        val hourlyBean by weatherModel.hourlyBean.observeAsState()
        val airBean by weatherModel.airBean.observeAsState()
        val loading by weatherModel.loading.observeAsState(false)
        SwipeToRefreshLayout(
            refreshingState = loading,
            onRefresh = {
                lifecycleScope.launch {
                    weatherModel.address.value?.let {
                        weatherModel.getWeatherData("${it.latitude}:${it.longitude}")
                    }
                }
            },
            refreshIndicator = {
                Surface(elevation = 10.dp, shape = CircleShape) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(36.dp)
                            .padding(6.dp)
                    )
                }
            }
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(color = BgGray)
                    .verticalScroll(rememberScrollState())
            ) {
                NowAndHourly(dailyBean, nowBean, hourlyBean)
                AirView(airBean)
                DailyView(dailyBean)
                OtherView(nowBean)
            }
        }
    }

    @Composable
    private fun NowAndHourly(dailyBean: DailyBean?, nowBean: NowBean?, hourlyBean: HourlyBean?) {
        val today: Daily? = dailyBean?.daily?.get(1)
        Column(
            modifier = columnModifier
        ) {
            nowBean?.let {
                GrayTextSmall(text = currentTime())
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = resourceId("w${it.now.code}")),
                            contentDescription = "",
                            Modifier.size(50.dp)
                        )
                        BlackTextBigger(text = "${it.now.temperature}°")
                    }
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        GrayTextSmaller(text = it.now.text)
                        today?.let {
                            GrayTextSmaller(text = "${it.high}°/${it.low}°")
                        }
                        GrayTextSmaller(text = "体感温度${it.now.feels_like}°")
                    }
                }
            }
            hourlyBean?.let {
                LazyRow(
                    Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(top = 4.dp)
                ) {
                    itemsIndexed(items = it.hourly) {pos, item ->
                        Column(
                            Modifier.padding(start = if (pos == 0) 0.dp else 30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            GrayTextSmall(text = item.time.toHourString())
                            Image(
                                painter = painterResource(id = resourceId("w${item.code}")),
                                contentDescription = "",
                                Modifier
                                    .height(30.dp)
                                    .padding(top = 10.dp)
                            )
                            BlackTextNormal(
                                Modifier.padding(top = 8.dp),
                                text = "${item.temperature}°"
                            )
                            Row(
                                Modifier.padding(top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_opacity),
                                    contentDescription = "",
                                    Modifier
                                        .size(12.dp)
                                        .padding(end = 2.dp),
                                    Blue
                                )
                                GrayTextSmaller(text = "${if (item.humidity.isEmpty()) 0 else item.humidity}%")
                            }
                            Icon(
                                painter = painterResource(id = getArrowId(item.wind_direction)),
                                contentDescription = "",
                                Modifier.padding(top = 15.dp),
                                Blue
                            )
                            GrayTextSmaller(
                                Modifier.padding(top = 8.dp),
                                text = "${item.wind_speed}千米/时"
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AirView(airBean: AirQualityBean?) {
        Row(
            modifier = columnModifier
        ) {
            airBean?.let {
                Row(
                    Modifier
                        .weight(0.5f)
                        .padding(start = 10.dp)
                ) {
                    Row {
                        DrawLine(it.air.city.aqi.toFloat(), 500f)
                        Column {
                            GrayTextNormal(text = "AQI") 
                            BlackTextNormal(
                                Modifier.padding(top = 6.dp),
                                text = it.air.city.quality
                            )
                            BlackTextNormal(
                                Modifier.padding(top = 6.dp),
                                text = it.air.city.aqi
                            )
                        }
                    }
                }
                Row(
                    Modifier
                        .weight(0.5f)
                        .padding(start = 10.dp)
                ) {
                    Row {
                        DrawLine(it.air.city.pm25.toFloat(), 300f)
                        Column {
                            GrayTextNormal(text = "PM2.5")
                            BlackTextNormal(
                                Modifier.padding(top = 6.dp),
                                text = it.air.city.quality
                            )
                            BlackTextNormal(
                                Modifier.padding(top = 6.dp),
                                text = "${it.air.city.pm25} 微克/立方米"
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun DrawLine(value: Float, max: Float, offset: Float = 8.dpToPx()) {
        Canvas(modifier = Modifier
            .height(64.dp)
            .padding(end = 15.dp)) {
            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    color = LightBlue
                    style = PaintingStyle.Stroke
                    strokeWidth = offset
                    strokeCap = StrokeCap.Round
                }
                canvas.drawLine(Offset(0f, offset), Offset(0f, size.height), paint)
                paint.color = Purple500
                val finalHeight = offset + (size.height - offset) * (1 - 1 / max * value)
                canvas.drawLine(Offset(0f, size.height), Offset(0f, finalHeight), paint)
            }
        }
    }

    @Composable
    private fun DailyView(dailyBean: DailyBean?) {
        Box(
            modifier = columnModifier
        ) {
            dailyBean?.let {
                Column(Modifier.fillMaxWidth()) {
                    repeat(dailyBean.daily.size) { pos ->
                        val item = it.daily[pos]
                        if (pos == 0) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                GrayTextSmall(text = "昨天")
                                GrayTextSmall(text = "${item.high}°/${item.low}°")
                            }
                        } else {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 15.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BlackTextNormal(
                                    Modifier.width(50.dp),
                                    text = item.date.dayOfWeek()
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_opacity),
                                            contentDescription = "",
                                            Modifier
                                                .size(12.dp)
                                                .padding(end = 2.dp),
                                            Blue
                                        )
                                        GrayTextSmaller(text = "${if (item.humidity.isEmpty()) 0 else item.humidity}%")
                                    }
                                    Image(
                                        painter = painterResource(id = resourceId("w${item.code_day}")),
                                        contentDescription = "",
                                        Modifier
                                            .width(45.dp)
                                            .padding(start = 20.dp),
                                        contentScale = ContentScale.FillWidth
                                    )
                                    Image(
                                        painter = painterResource(id = resourceId("w${item.code_night}")),
                                        contentDescription = "",
                                        Modifier
                                            .width(30.dp)
                                            .padding(start = 5.dp),
                                        contentScale = ContentScale.FillWidth
                                    )
                                }
                                BlackTextNormal(
                                    Modifier.width(50.dp),
                                    text = "${item.high}°/${item.low}°",
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun OtherView(nowBean: NowBean?) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(25.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_opacity),
                        contentDescription = "",
                        Modifier.padding(end = 8.dp),
                        Blue
                    )
                    GrayTextNormal(
                        text = "湿度"
                    )
                }
                nowBean?.let {
                    BlackTextNormal(text = "${it.now.humidity}%")
                }
            }
        }
    }

    private fun getArrowId(name: String): Int {
        return when(name) {
            "北" -> R.drawable.ic_north
            "西北" -> R.drawable.ic_north_west
            "东北" -> R.drawable.ic_north_east
            "南" -> R.drawable.ic_south
            "西南" -> R.drawable.ic_south_west
            "东南" -> R.drawable.ic_south_east
            "西" -> R.drawable.ic_west
            "东" -> R.drawable.ic_east
            else -> R.drawable.ic_horizontal_rule
        }
    }
}

@Composable
fun GrayTextSmaller(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        color = TextGray,
        fontSize = 10.sp
    )
}

@Composable
fun GrayTextSmall(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        color = TextGray,
        fontSize = 12.sp
    )
}

@Composable
fun GrayTextNormal(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        color = TextGray,
        fontSize = 14.sp
    )
}

@Composable
fun BlackTextSmall(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        color = TextBlack,
        fontSize = 12.sp
    )
}

@Composable
fun BlackTextNormal(modifier: Modifier = Modifier, text: String, textAlign: TextAlign = TextAlign.Start) {
    Text(
        modifier = modifier,
        text = text,
        color = TextBlack,
        fontSize = 14.sp,
        textAlign = textAlign
    )
}

@Composable
fun BlackTextBigger(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        color = TextBlack,
        fontSize = 50.sp
    )
}